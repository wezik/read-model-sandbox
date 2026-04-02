package dev.wezik.sandbox.domain.event

class OutboxEventPublisher<T : Event>(
  private val outbox: Outbox<T>,
) : EventPublisher<T> {

  override suspend fun publish(event: T) {
    outbox.save(event)
  }
}
