package dev.wezik.sandbox.domain

interface MappingRepository {
  suspend fun list(): Map<Attribute, List<Mapping>>
  suspend fun update(attribute: Attribute, mapping: Mapping)
}
