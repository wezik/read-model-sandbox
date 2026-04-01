package dev.wezik.sandbox.domain

import java.util.UUID

data class Product(
  val id: UUID,
  val assortmentId: UUID,
  val versions: VersionedAttributes,
  val styles: List<Style>,
)

data class Style(
  val id: UUID,
  val assortmentId: UUID,
  val versions: VersionedAttributes,
  val articles: List<Article>,
)

data class Article(
  val id: UUID,
  val assortmentId: UUID,
  val versions: VersionedAttributes,
)

data class VersionedAttributes(
  val local: Map<String, String>,
  val sent: Map<String, String>,
)
