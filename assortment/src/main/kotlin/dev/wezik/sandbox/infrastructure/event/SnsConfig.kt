package dev.wezik.sandbox.infrastructure.event

import aws.sdk.kotlin.services.sns.SnsClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.collections.Attributes
import aws.smithy.kotlin.runtime.net.url.Url
import com.fasterxml.jackson.databind.ObjectMapper
import dev.wezik.sandbox.domain.ProductEvent
import dev.wezik.sandbox.domain.event.BatchEventPublisher
import dev.wezik.sandbox.domain.event.EventPublisher
import dev.wezik.sandbox.domain.event.Outbox
import dev.wezik.sandbox.domain.event.OutboxEventPublisher
import dev.wezik.sandbox.domain.event.OutboxRelay
import org.jooq.DSLContext
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ProductEventPublisherProperties::class)
class SnsConfig(private val properties: ProductEventPublisherProperties) {

  @Bean
  fun snsClient(): SnsClient = SnsClient {
    region = properties.region
    properties.endpointOverride?.takeIf { it.isNotBlank() }?.let {
      endpointUrl = Url.parse(it)
      credentialsProvider = StaticCredentialsProvider(
        Credentials("test", "test")
      )
    }
  }

  @Bean(initMethod = "initialize")
  fun productEventOutbox(
    dsl: DSLContext,
    objectMapper: ObjectMapper,
  ): PostgresOutbox<ProductEvent> = PostgresOutbox(dsl, objectMapper, ProductEvent::class.java)

  @Bean
  fun productEventPublisher(outbox: Outbox<ProductEvent>): EventPublisher<ProductEvent> =
    OutboxEventPublisher(outbox)

  @Bean(initMethod = "start", destroyMethod = "shutdown")
  fun productEventOutboxRelay(
    outbox: Outbox<ProductEvent>,
    batchPublisher: BatchEventPublisher<ProductEvent>,
    objectMapper: ObjectMapper,
  ): OutboxRelay<ProductEvent> = OutboxRelay(
    outbox = outbox,
    batchPublisher = batchPublisher,
    estimateSize = { event -> objectMapper.writeValueAsBytes(event.toSnsMessage()).size },
    pollIntervalMs = properties.outbox.pollIntervalMs,
    batchSize = properties.outbox.batchSize,
    maxBatchBytes = properties.outbox.maxBatchBytes,
  )
}

private class StaticCredentialsProvider(private val credentials: Credentials) : CredentialsProvider {
  override suspend fun resolve(attributes: Attributes): Credentials = credentials
}
