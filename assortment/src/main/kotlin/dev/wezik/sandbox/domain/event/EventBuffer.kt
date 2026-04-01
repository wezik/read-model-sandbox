package dev.wezik.sandbox.domain.event

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventBuffer<T : Event>(
  private val maxBatchCount: Int = 50,
  private val maxBatchBytes: Int = 200_000,
  private val flushIntervalMs: Long,
  private val estimateSize: (T) -> Int,
  private val onFlush: suspend (List<T>) -> Unit,
) {
  private val logger = KotlinLogging.logger {}
  private val mutex = Mutex()
  private val buffers = mutableMapOf<String, MutableList<T>>()
  private val bufferSizes = mutableMapOf<String, Int>()

  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private var flushJob: Job? = null

  fun start() {
    flushJob = scope.launch {
      while (isActive) {
        delay(flushIntervalMs)
        flushAll()
      }
    }
  }

  suspend fun add(event: T) {
    mutex.withLock {
      val key = event.key
      val eventSize = estimateSize(event)
      val currentSize = bufferSizes.getOrDefault(key, 0)

      // Flush if adding this event would exceed limits
      if (currentSize + eventSize > maxBatchBytes) {
        logger.debug { "Batch size limit reached for $key (${currentSize + eventSize} bytes), flushing" }
        flushBufferLocked(key)
      }

      val buffer = buffers.getOrPut(key) { mutableListOf() }
      buffer.add(event)
      bufferSizes[key] = bufferSizes.getOrDefault(key, 0) + eventSize
      logger.debug { "Buffered $key event (${bufferSizes[key]} bytes total)" }

      // Also flush if count limit reached
      if (buffer.size >= maxBatchCount) {
        logger.debug { "Batch count limit reached for $key (${buffer.size} events), flushing" }
        flushBufferLocked(key)
      }
    }
  }

  suspend fun flushAll() {
    mutex.withLock {
      buffers.keys.toList().forEach { key ->
        flushBufferLocked(key)
      }
    }
  }

  private suspend fun flushBufferLocked(key: String) {
    val buffer = buffers[key] ?: return
    if (buffer.isEmpty()) return
    val batch = buffer.toList()
    val batchSize = bufferSizes[key] ?: 0
    buffer.clear()
    bufferSizes[key] = 0
    logger.info { "Flushing ${batch.size} $key events (~$batchSize bytes)" }
    onFlush(batch)
  }

  fun shutdown() {
    logger.info { "Shutting down EventBuffer, flushing remaining events..." }
    runBlocking { flushAll() }
    flushJob?.cancel()
    scope.cancel()
  }
}
