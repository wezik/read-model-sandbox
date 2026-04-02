package dev.wezik.sandbox.domain.event

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import java.util.UUID

class OutboxRelay<T : Event>(
  private val outbox: Outbox<T>,
  private val batchPublisher: BatchEventPublisher<T>,
  private val estimateSize: (T) -> Int,
  private val nodeId: String = UUID.randomUUID().toString(),
  private val pollIntervalMs: Long = 1000,
  private val batchSize: Int = 100,
  private val maxBatchBytes: Int = 200_000,
) {
  private val logger = KotlinLogging.logger {}
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private var pollJob: Job? = null
  private var consecutiveEmpty = 0

  fun start() {
    logger.info { "Starting OutboxRelay with nodeId=$nodeId" }

    pollJob = scope.launch {
      while (isActive) {
        try {
          val processed = poll()
          if (processed == 0) {
            consecutiveEmpty++
            // Backoff when idle: up to 10x poll interval
            delay(pollIntervalMs * minOf(consecutiveEmpty, 10))
          } else {
            consecutiveEmpty = 0
            delay(pollIntervalMs)
          }
        } catch (e: Exception) {
          logger.error(e) { "Error polling outbox" }
          delay(pollIntervalMs)
        }
      }
    }
  }

  private suspend fun poll(): Int {
    logger.debug { "Polling outbox for up to $batchSize entries..." }
    val entries = outbox.claimBatch(nodeId, batchSize)
    logger.debug { "Claimed ${entries.size} entries from outbox" }
    if (entries.isEmpty()) return 0

    var processed = 0
    val byKey = entries.groupBy { it.event.key }

    for ((key, keyEntries) in byKey) {
      val chunks = chunkBySize(keyEntries)

      for (chunk in chunks) {
        try {
          val events = chunk.map { it.event }
          batchPublisher.publishBatch(events)
          outbox.markPublished(chunk.map { it.id })
          processed += chunk.size
          logger.debug { "Published ${events.size} $key events from outbox" }
        } catch (e: Exception) {
          logger.error(e) { "Failed to publish $key events chunk, releasing claim" }
          outbox.releaseClaim(chunk.map { it.id })
        }
      }
    }

    return processed
  }

  private fun chunkBySize(entries: List<OutboxEntry<T>>): List<List<OutboxEntry<T>>> {
    val chunks = mutableListOf<List<OutboxEntry<T>>>()
    var currentChunk = mutableListOf<OutboxEntry<T>>()
    var currentSize = 0

    for (entry in entries) {
      val eventSize = estimateSize(entry.event)

      if (currentChunk.isNotEmpty() && currentSize + eventSize > maxBatchBytes) {
        chunks.add(currentChunk)
        currentChunk = mutableListOf()
        currentSize = 0
      }

      currentChunk.add(entry)
      currentSize += eventSize
    }

    if (currentChunk.isNotEmpty()) {
      chunks.add(currentChunk)
    }

    return chunks
  }

  fun shutdown() {
    logger.info { "Shutting down OutboxRelay nodeId=$nodeId..." }
    pollJob?.cancel()
    scope.cancel()
  }
}
