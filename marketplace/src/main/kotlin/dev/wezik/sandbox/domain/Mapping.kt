package dev.wezik.sandbox.domain

// NOTE: This is a simplified version of marketplace specific mappings that we have to corelate
// here its just hardcoded but usually it would be fetched from some external source
val attributeMappings = mapOf(
  Attribute("color") to TargetAttribute("color"),
  Attribute("size") to TargetAttribute("size_chart"),
  Attribute("category") to TargetAttribute("type"),
)

val allowedValues = mapOf(
  TargetAttribute("color") to listOf("red", "blue", "green", "black", "white"),
  TargetAttribute("size_chart") to listOf("S", "M", "L", "XL"),
  TargetAttribute("type") to listOf("t-shirt", "sweater", "jacket", "pants", "shoes", "hats"),
)

data class Mapping(
  val from: String,
  val to: String?,
) {
  fun isMapped() = to != null
}

@JvmInline
value class Attribute(val value: String)

@JvmInline
value class TargetAttribute(val value: String)

data class MappingContext(
  val attribute: Attribute,
  val targetAttribute: TargetAttribute,
  val mapping: Mapping,
)
