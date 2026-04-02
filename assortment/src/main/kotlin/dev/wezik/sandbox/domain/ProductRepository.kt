package dev.wezik.sandbox.domain

import java.util.UUID

interface ProductRepository {
  suspend fun list(): List<Product>
  suspend fun create(product: Product): Product
  suspend fun findById(id: UUID): Product?
}
