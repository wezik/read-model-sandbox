package dev.wezik.sandbox.infrastructure.event

import dev.wezik.sandbox.domain.Product
import dev.wezik.sandbox.domain.ProductEvent
import java.time.Instant
import java.util.UUID

data class SnsProductEventMessage(
  val occurredAt: Instant,
  val product: Product,
)

fun ProductEvent.toSnsMessage(): SnsProductEventMessage {
  return SnsProductEventMessage(
    occurredAt = Instant.now(),
    product = product,
  )
}
