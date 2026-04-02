package dev.wezik.sandbox.infrastructure.event

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "events")
data class ProductEventPublisherProperties(
  val topicArn: String = "",
  val region: String = "us-east-1",
  val endpointOverride: String? = null,
  val outbox: OutboxProperties = OutboxProperties(),
)

data class OutboxProperties(
  val pollIntervalMs: Long = 1000,
  val batchSize: Int = 100,
  val maxBatchBytes: Int = 200_000,
  val retentionHours: Long = 24,
)
