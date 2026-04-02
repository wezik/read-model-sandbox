package dev.wezik.sandbox.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import dev.wezik.sandbox.domain.Product
import dev.wezik.sandbox.domain.ProductRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PostgresProductRepository(
  private val dsl: DSLContext,
  private val objectMapper: ObjectMapper,
) : ProductRepository {

  private val logger = KotlinLogging.logger {}

  private val products = table("products")
  private val id = field("id", SQLDataType.UUID)
  private val data = field("data", SQLDataType.JSONB)

  fun initialize() {
    dsl.execute("""
      CREATE TABLE IF NOT EXISTS products (
        id UUID PRIMARY KEY,
        data JSONB NOT NULL
      )
    """)
    logger.info { "Products table initialized" }
  }

  override suspend fun list(): List<Product> =
    dsl.selectFrom(products)
      .fetch()
      .map { record ->
        objectMapper.readValue(record.get(data).toString(), Product::class.java)
      }

  override suspend fun create(product: Product): Product {
    dsl.insertInto(products)
      .set(id, product.id)
      .set(data, field("?::jsonb", SQLDataType.JSONB, objectMapper.writeValueAsString(product)))
      .execute()
    logger.debug { "Created product ${product.id}" }
    return product
  }

  override suspend fun findById(id: UUID): Product? =
    dsl.selectFrom(products)
      .where(this.id.eq(id))
      .fetchOne()
      ?.let { record ->
        objectMapper.readValue(record.get(data).toString(), Product::class.java)
      }
}
