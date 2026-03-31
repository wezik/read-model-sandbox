package dev.wezik.sandbox.domain

interface ProductRepository {
  suspend fun list(): List<Product>
}
