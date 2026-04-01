package dev.wezik.sandbox.domain.event

import dev.wezik.sandbox.domain.ProductEvent

interface BatchEventPublisher<T : Event> {
  suspend fun publishBatch(events: List<T>)
}
