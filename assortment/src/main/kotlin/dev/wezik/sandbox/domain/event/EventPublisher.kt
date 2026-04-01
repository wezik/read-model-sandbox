package dev.wezik.sandbox.domain.event

fun interface EventPublisher<T : Event> {
  suspend fun publish(event: T)
}
