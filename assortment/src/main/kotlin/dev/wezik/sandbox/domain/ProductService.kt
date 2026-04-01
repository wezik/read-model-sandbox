package dev.wezik.sandbox.domain

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wezik.sandbox.domain.event.EventPublisher
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
  private val eventPublisher: EventPublisher<ProductEvent>,
) {

  private val logger = KotlinLogging.logger {}

  suspend fun getProducts(): List<Product> {
    logger.debug { "Listing all products" }
    return repository.list()
  }

  suspend fun create(command: CreateProductCommand): Product {
    logger.debug { "Creating product $command" }
    val product = repository.create(command.toProduct())
    eventPublisher.publish(ProductEvent.Created(product))
    return product
  }
}
