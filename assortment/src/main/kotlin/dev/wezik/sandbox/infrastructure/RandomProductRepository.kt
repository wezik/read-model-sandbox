package dev.wezik.sandbox.infrastructure

import dev.wezik.sandbox.domain.Article
import dev.wezik.sandbox.domain.Product
import dev.wezik.sandbox.domain.ProductRepository
import dev.wezik.sandbox.domain.Style
import dev.wezik.sandbox.domain.Category
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RandomProductRepository : ProductRepository {

  override suspend fun list() = (1..5).map { randomProduct() }

  private fun randomProduct(styleCount: Int = 5) =
    Product(
      id = UUID.randomUUID(),
      styles = (1..styleCount).map { randomStyle(5) },
      category = randomCategory(),
    )

  private fun randomCategory() = listOf(Category.CAP, Category.DRESS, Category.JEANS, Category.T_SHIRT).random()

  private fun randomStyle(articleCount: Int = 5) =
    Style(
      id = UUID.randomUUID(),
      articles = (1..articleCount).map { randomArticle() },
      color = randomColor(),
    )

  private fun randomColor() = listOf("black", "white", "red", "blue").random()

  private fun randomArticle() = Article(
    id = UUID.randomUUID(),
    size = randomSize(),
  )

  private fun randomSize() = listOf("S", "M", "L", "XL").random()
}
