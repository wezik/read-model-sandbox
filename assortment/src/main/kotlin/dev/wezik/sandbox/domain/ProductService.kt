package dev.wezik.sandbox.domain

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

data class CreateProductCommand(
  val category: Category,
  val styles: List<CreateStyleCommand>,
)

data class CreateStyleCommand(
  val color: String,
  val articles: List<CreateArticleCommand>,
)

data class CreateArticleCommand(
  val size: String,
)

private fun CreateProductCommand.toProduct() = Product(
  id = UUID.randomUUID(),
  category = category,
  styles = styles.map { it.toProduct() },
)

private fun CreateStyleCommand.toProduct() = Style(
  id = UUID.randomUUID(),
  color = color,
  articles = articles.map { it.toProduct() },
)

private fun CreateArticleCommand.toProduct() = Article(
  id = UUID.randomUUID(),
  size = size,
)

@Service
class ProductService(
  private val repository: ProductRepository,
) {

  private val logger = KotlinLogging.logger {}

  suspend fun getProducts(): List<Product> {
    logger.debug { "Listing all products" }
    return repository.list()
  }

  suspend fun create(command: CreateProductCommand): Product {
    logger.debug { "Creating product $command" }
    return command.toProduct()
  }
}
