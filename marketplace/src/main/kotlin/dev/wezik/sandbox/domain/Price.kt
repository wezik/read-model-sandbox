package dev.wezik.sandbox.domain

import org.joda.money.Money
import java.util.UUID

data class Price(
  val id: UUID,
  val assortmentId: UUID,
  val channel: MarketplaceChannel,
  val versions: VersionedPrice,
)

data class VersionedPrice(
  val local: Money,
  val sent: Money?,
)
