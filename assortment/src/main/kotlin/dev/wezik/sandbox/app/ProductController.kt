package dev.wezik.sandbox.app

import dev.wezik.sandbox.domain.ProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(private val service: ProductService) {

  @GetMapping
  suspend fun list() = service.getProducts()

  @PostMapping
  suspend fun create(@RequestBody dto: CreateProductDto) = service.create(dto.toDomain())
}
