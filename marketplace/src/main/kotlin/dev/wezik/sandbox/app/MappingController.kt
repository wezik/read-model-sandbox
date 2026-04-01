package dev.wezik.sandbox.app

import dev.wezik.sandbox.domain.MappingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import dev.wezik.sandbox.domain.Attribute

@RestController
@RequestMapping("/mappings")
class MappingController(
  private val mappingService: MappingService,
) {
  @GetMapping
  suspend fun list() = mappingService.list()

  @PostMapping
  suspend fun map(@RequestBody dto: CreateMappingDto) = mappingService.map(Attribute(dto.attribute), dto.from, dto.to)
}
