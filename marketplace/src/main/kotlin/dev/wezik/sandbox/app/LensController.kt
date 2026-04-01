package dev.wezik.sandbox.app

import dev.wezik.sandbox.domain.LensService
import dev.wezik.sandbox.domain.MarketplaceChannel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/lens")
class LensController(
  private val lensService: LensService,
) {
  @GetMapping
  suspend fun getLens(@RequestParam channel: MarketplaceChannel) = lensService.list(channel)
}
