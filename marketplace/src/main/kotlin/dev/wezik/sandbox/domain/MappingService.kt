package dev.wezik.sandbox.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class MappingService(
  private val repository: MappingRepository,
) {

  private val logger = KotlinLogging.logger {}

  suspend fun list(): Map<Attribute, List<Mapping>> = repository.list()

  suspend fun map(attribute: Attribute, from: String, to: String?) {
    val marketplaceAttribute = attributeMappings[attribute] ?: error("Attribute $attribute is not supported")

    if (to !in allowedValues[marketplaceAttribute]!!) {
      error("value $to is not a valid marketplace value for attribute $attribute ($marketplaceAttribute)")
    }

    logger.info { "Mapping $attribute $from=$to" }
    repository.update(attribute, Mapping(from, to))
  }
}
