package dev.wezik.sandbox.domain

import dev.wezik.sandbox.domain.event.Event

sealed interface ProductEvent : Event {
  val product: Product
  data class Created(override val product: Product) : ProductEvent
  data class Updated(override val product: Product) : ProductEvent
}
