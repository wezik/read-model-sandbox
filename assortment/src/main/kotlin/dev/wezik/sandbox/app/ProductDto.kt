package dev.wezik.sandbox.app

import dev.wezik.sandbox.domain.Category

data class CreateProductDto(
  val category: Category,
  val styles: List<CreateStyleDto>,
)

data class CreateStyleDto(
  val color: String,
  val articles: List<CreateArticleDto>,
)

data class CreateArticleDto(
  val size: String,
)
