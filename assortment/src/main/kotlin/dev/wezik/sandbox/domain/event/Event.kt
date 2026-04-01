package dev.wezik.sandbox.domain.event

interface Event {
  val key: String get() = this::class.simpleName!!
}
