package dev.wezik.sandbox.infrastructure.event

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wezik.sandbox.domain.event.Event
import dev.wezik.sandbox.domain.event.Outbox
import dev.wezik.sandbox.domain.event.OutboxEntry
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType
import java.time.Instant
import java.util.UUID

class PostgresOutbox<T : Event>(
  private val dsl: DSLContext,
  private val objectMapper: ObjectMapper,
  private val eventClass: Class<T>,
) : Outbox<T> {

  private val logger = KotlinLogging.logger {}

  private val outbox = table("outbox")
  private val id = field("id", SQLDataType.UUID)
  private val eventType = field("event_type", SQLDataType.VARCHAR(255))
  private val eventData = field("event_data", SQLDataType.JSONB)
  private val createdAt = field("created_at", SQLDataType.TIMESTAMPWITHTIMEZONE)
  private val claimedBy = field("claimed_by", SQLDataType.VARCHAR(255))
  private val claimedAt = field("claimed_at", SQLDataType.TIMESTAMPWITHTIMEZONE)
  private val publishedAt = field("published_at", SQLDataType.TIMESTAMPWITHTIMEZONE)

  fun initialize() {
    dsl.execute("""
      CREATE TABLE IF NOT EXISTS outbox (
        id UUID PRIMARY KEY,
        event_type VARCHAR(255) NOT NULL,
        event_data JSONB NOT NULL,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        claimed_by VARCHAR(255),
        claimed_at TIMESTAMPTZ,
        published_at TIMESTAMPTZ
      )
    """)

    dsl.execute("""
      CREATE INDEX IF NOT EXISTS idx_outbox_unpublished
      ON outbox (created_at)
      WHERE published_at IS NULL
    """)

    logger.info { "Outbox table initialized" }
  }

  override suspend fun save(event: T): OutboxEntry<T> {
    val entry = OutboxEntry(
      id = UUID.randomUUID(),
      event = event,
      createdAt = Instant.now(),
    )

    dsl.insertInto(outbox)
      .set(id, entry.id)
      .set(eventType, event::class.simpleName)
      .set(eventData, field("?::jsonb", SQLDataType.JSONB, objectMapper.writeValueAsString(event)))
      .set(createdAt, entry.createdAt.atOffset(java.time.ZoneOffset.UTC))
      .execute()

    logger.debug { "Saved event ${entry.id} to outbox" }
    return entry
  }

  override suspend fun claimBatch(nodeId: String, limit: Int): List<OutboxEntry<T>> {
    logger.debug { "Attempting to claim batch for node $nodeId, limit $limit" }
    // Two-step claim: SELECT FOR UPDATE SKIP LOCKED, then UPDATE
    return dsl.transactionResult { config ->
      val tx = using(config)

      // Step 1: Lock and get IDs
      val ids = tx.select(id)
        .from(outbox)
        .where(publishedAt.isNull)
        .and(claimedBy.isNull)
        .orderBy(createdAt)
        .limit(limit)
        .forUpdate()
        .skipLocked()
        .fetch(id)

      if (ids.isEmpty()) {
        logger.debug { "No unclaimed entries found" }
        return@transactionResult emptyList()
      }

      // Step 2: Claim them
      tx.update(outbox)
        .set(claimedBy, nodeId)
        .set(claimedAt, currentOffsetDateTime())
        .where(id.`in`(ids))
        .execute()

      // Step 3: Fetch full records
      val claimed = tx.selectFrom(outbox)
        .where(id.`in`(ids))
        .fetch()

      logger.debug { "Claimed ${claimed.size} entries" }
      claimed.map { record -> toEntry(record) }
    }
  }

  override suspend fun markPublished(ids: List<UUID>) {
    if (ids.isEmpty()) return
    dsl.update(outbox)
      .set(publishedAt, currentOffsetDateTime())
      .setNull(claimedBy)
      .setNull(claimedAt)
      .where(id.`in`(ids))
      .execute()
    logger.debug { "Marked ${ids.size} entries as published" }
  }

  override suspend fun releaseClaim(ids: List<UUID>) {
    if (ids.isEmpty()) return
    dsl.update(outbox)
      .setNull(claimedBy)
      .setNull(claimedAt)
      .where(id.`in`(ids))
      .execute()
    logger.debug { "Released claim on ${ids.size} entries" }
  }

  override suspend fun deletePublishedBefore(instant: Instant) {
    val deleted = dsl.deleteFrom(outbox)
      .where(publishedAt.isNotNull)
      .and(publishedAt.lt(instant.atOffset(java.time.ZoneOffset.UTC)))
      .execute()
    if (deleted > 0) {
      logger.info { "Cleaned up $deleted published outbox entries" }
    }
  }

  private fun toEntry(record: org.jooq.Record): OutboxEntry<T> = OutboxEntry(
    id = record.get(id)!!,
    event = objectMapper.readValue(record.get(eventData).toString(), eventClass),
    createdAt = record.get(createdAt)!!.toInstant(),
    claimedBy = record.get(claimedBy),
    claimedAt = record.get(claimedAt)?.toInstant(),
    publishedAt = record.get(publishedAt)?.toInstant(),
  )
}
