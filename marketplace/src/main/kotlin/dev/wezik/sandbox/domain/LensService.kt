package dev.wezik.sandbox.domain

import org.joda.money.Money
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class LensService {
  suspend fun list(channel: MarketplaceChannel): List<ProductLens> {
    val products = (1..10).map { someProduct() }
    val articleIds = products.flatMap { it.styles }.flatMap { it.articles }.map { it.assortmentId }
    val prices = (1..20).map { somePrice(articleIds.random()) }
    return products.map { ProductLens.from(it, channel, prices) }
  }

  private fun someProduct(): Product {
    return Product(
      id = UUID.randomUUID(),
      assortmentId = UUID.randomUUID(),
      versions = VersionedAttributes(
        local = mapOf("title" to "Some title"),
        sent = mapOf("title" to "Some title"),
      ),
      styles = listOf(someStyle()),
    )
  }

  private fun someStyle(): Style {
    return Style(
      id = UUID.randomUUID(),
      assortmentId = UUID.randomUUID(),
      versions = VersionedAttributes(
        local = mapOf("title" to "Some title"),
        sent = mapOf("title" to "Some title"),
      ),
      articles = listOf(someArticle()),
    )
  }

  private fun someArticle(): Article {
    return Article(
      id = UUID.randomUUID(),
      assortmentId = UUID.randomUUID(),
      versions = VersionedAttributes(
        local = mapOf("title" to "Some title"),
        sent = mapOf("title" to "Some title"),
      ),
    )
  }

  private fun somePrice(assortmentId: UUID): Price {
    val channel = someChannel()
    return Price(
      id = UUID.randomUUID(),
      assortmentId = assortmentId,
      channel = channel,
      versions = VersionedPrice(
        local = Money.of(channel.currency, BigDecimal.TEN),
        sent = Money.of(channel.currency, BigDecimal.TEN),
      )
    )
  }

  private fun someChannel(): MarketplaceChannel {
    return listOf(MarketplaceChannel.SANDBOX, MarketplaceChannel.POLAND, MarketplaceChannel.GERMANY).random()
  }
}
