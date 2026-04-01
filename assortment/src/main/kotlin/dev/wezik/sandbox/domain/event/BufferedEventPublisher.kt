package dev.wezik.sandbox.domain.event

class BufferedEventPublisher<T : Event>(
  private val buffer: EventBuffer<T>,
) : EventPublisher<T> {

  override suspend fun publish(event: T) {
    buffer.add(event)
  }
}
