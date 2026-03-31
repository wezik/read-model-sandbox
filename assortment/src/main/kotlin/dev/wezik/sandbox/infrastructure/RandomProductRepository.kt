package dev.wezik.sandbox.infrastructure

import dev.wezik.sandbox.domain.Article
import dev.wezik.sandbox.domain.Product
import dev.wezik.sandbox.domain.ProductRepository
import dev.wezik.sandbox.domain.Style
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RandomProductRepository : ProductRepository {

  override suspend fun list() = (1..5).map { randomProduct() }

  private fun randomProduct(styleCount: Int = 5) =
    Product(UUID.randomUUID(), (1..styleCount).map { randomStyle(5) })

  private fun randomStyle(articleCount: Int = 5) =
    Style(UUID.randomUUID(), (1..articleCount).map { Article(UUID.randomUUID()) })

  private fun randomArticle() = Article(UUID.randomUUID())
}
