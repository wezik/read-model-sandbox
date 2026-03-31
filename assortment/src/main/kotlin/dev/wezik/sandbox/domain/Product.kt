package dev.wezik.sandbox.domain

import java.util.*

data class Product(
  val id: UUID,
  val category: Category,
  val styles: List<Style>,
)

data class Style(
  val id: UUID,
  val color: String,
  val articles: List<Article>,
)

data class Article(
  val size: String,
  val id: UUID,
)

enum class Category {
  CAP,
  DRESS,
  JEANS,
  T_SHIRT,
}
