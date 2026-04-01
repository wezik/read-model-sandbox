package dev.wezik.sandbox.app

data class CreateMappingDto(
  val attribute: String,
  val from: String,
  val to: String?,
)
