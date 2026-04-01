package dev.wezik.sandbox.domain

import java.util.Locale
import org.joda.money.CurrencyUnit

enum class MarketplaceChannel(
  val externalId: String,
  val country: String,
  val currency: CurrencyUnit,
) {
  SANDBOX(
    "sandbox",
    "SANDBOX",
    CurrencyUnit.EUR,
  ),
  POLAND(
    "poland",
    "POLAND",
    CurrencyUnit.of("PLN"),
  ),
  GERMANY(
    "germany",
    "GERMANY",
    CurrencyUnit.EUR,
  ),
}
