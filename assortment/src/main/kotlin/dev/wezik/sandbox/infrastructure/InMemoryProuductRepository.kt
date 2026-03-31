package dev.wezik.sandbox.infrastructure

import dev.wezik.sandbox.domain.Product
import dev.wezik.sandbox.domain.ProductRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryProuductRepository: ProductRepository {

  private val products = mutableMapOf<UUID, Product>()

  override suspend fun list() = products.values.toList()

  override suspend fun create(product: Product): Product {
    products[product.id] = product
    return products[product.id]!!
  }
}
