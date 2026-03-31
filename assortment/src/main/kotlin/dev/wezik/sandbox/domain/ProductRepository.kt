package dev.wezik.sandbox.domain

interface ProductRepository {
  suspend fun list(): List<Product>
  suspend fun create(product: Product): Product
}
