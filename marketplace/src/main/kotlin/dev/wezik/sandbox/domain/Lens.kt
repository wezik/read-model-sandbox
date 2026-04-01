package dev.wezik.sandbox.domain

import java.util.UUID

// NOTE: Lens connects product tree to its fulfillment data and derives certain properties from it, like status.

data class ProductLens(
  val id: UUID,
  val assortmentId: UUID,
  val versions: VersionedAttributes,
  val styles: List<StyleLens>,
) {
  companion object {
    fun from(product: Product, channel: MarketplaceChannel, prices: List<Price>): ProductLens {
      val prices = prices.filter { it.channel == channel }
      val styles = product.styles.map { style ->
        val articles = style.articles.map { article ->
          val price = prices.find { it.assortmentId == article.assortmentId }
          ArticleLens(
            id = article.id,
            assortmentId = article.assortmentId,
            versions = article.versions,
            status = if (price != null) LensStatus.READY else LensStatus.INVALID,
            price = price
          )
        }

        StyleLens(
          style.id,
          style.assortmentId,
          style.versions,
          articles,
        )
      }
      return ProductLens(
        product.id,
        product.assortmentId,
        product.versions,
        styles,
      )
    }
  }
}

data class StyleLens(
  val id: UUID,
  val assortmentId: UUID,
  val versions: VersionedAttributes,
  val articles: List<ArticleLens>,
)

data class ArticleLens(
  val id: UUID,
  val assortmentId: UUID,
  val versions: VersionedAttributes,
  val price: Price?,

  // -- derived properties --
  val status: LensStatus,
)

enum class LensStatus {
  INVALID,
  OFFLINE,
  ONLINE,
  PENDING,
  READY,
}
