package dev.wezik.sandbox.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductService(
  private val repository: ProductRepository,
) {

  private val logger = KotlinLogging.logger {}

  suspend fun getProducts(): List<Product> {
    logger.debug { "Listing products..." }
    return repository.list()
  }
}
