package dev.wezik.sandbox.infrastructure.event

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "events")
data class ProductEventPublisherProperties(
  val topicArn: String = "",
  val maxBatchCount: Int = 50,
  val maxBatchBytes: Int = 200_000,
  val flushIntervalMs: Long = 500,
  val region: String = "us-east-1",
  val endpointOverride: String? = null,
)
