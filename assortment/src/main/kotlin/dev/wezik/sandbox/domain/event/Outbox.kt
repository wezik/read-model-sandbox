package dev.wezik.sandbox.domain.event

import java.time.Instant
import java.util.UUID

data class OutboxEntry<T : Event>(
  val id: UUID,
  val event: T,
  val createdAt: Instant,
  val claimedBy: String? = null,
  val claimedAt: Instant? = null,
  val publishedAt: Instant? = null,
)

interface Outbox<T : Event> {
  suspend fun save(event: T): OutboxEntry<T>
  suspend fun claimBatch(nodeId: String, limit: Int): List<OutboxEntry<T>>
  suspend fun markPublished(ids: List<UUID>)
  suspend fun releaseClaim(ids: List<UUID>)
  suspend fun deletePublishedBefore(instant: Instant)
}
