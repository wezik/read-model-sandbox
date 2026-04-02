package dev.wezik.sandbox.domain

import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.wezik.sandbox.domain.event.Event

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
sealed interface ProductEvent : Event {
  val product: Product
  data class Created(override val product: Product) : ProductEvent
  data class Updated(override val product: Product) : ProductEvent
}
