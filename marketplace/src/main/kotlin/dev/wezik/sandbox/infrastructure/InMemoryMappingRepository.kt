package dev.wezik.sandbox.infrastructure

import dev.wezik.sandbox.domain.MappingRepository
import org.springframework.stereotype.Repository
import dev.wezik.sandbox.domain.Attribute
import dev.wezik.sandbox.domain.Mapping

@Repository
class InMemoryMappingRepository : MappingRepository {

  private val mappings = mutableMapOf<Attribute, MutableList<Mapping>>()

  override suspend fun list(): Map<Attribute, List<Mapping>> {
    return mappings
  }

  override suspend fun update(attribute: Attribute, mapping: Mapping) {
    val existing = mappings.getOrPut(attribute) { mutableListOf() }
    for (existingMapping in existing) {
      if (existingMapping.from == mapping.from) {
        existing.remove(existingMapping)
        existing.add(mapping)
        mappings[attribute] = existing
        return
      }
    }
    existing.add(mapping)
    mappings[attribute] = existing
  }

}
