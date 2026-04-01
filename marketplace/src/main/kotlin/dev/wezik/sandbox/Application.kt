package dev.wezik.sandbox

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication
@ConfigurationPropertiesScan
class Application

fun main(args: Array<String>) {
  SpringApplicationBuilder(Application::class.java)
    .bannerMode(Banner.Mode.OFF)
    .run(*args)
}
