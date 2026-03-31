package dev.wezik.sandbox.app

import dev.wezik.sandbox.domain.CreateArticleCommand
import dev.wezik.sandbox.domain.CreateProductCommand
import dev.wezik.sandbox.domain.CreateStyleCommand

fun CreateProductDto.toDomain() = CreateProductCommand(
  category = category,
  styles = styles.map { it.toDomain() },
)

fun CreateStyleDto.toDomain() = CreateStyleCommand(
  color = color,
  articles = articles.map { it.toDomain() },
)

fun CreateArticleDto.toDomain() = CreateArticleCommand(
  size = size,
)
