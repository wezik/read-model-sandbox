package dev.wezik.sandbox.infrastructure.event

import aws.sdk.kotlin.services.sns.SnsClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.collections.Attributes
import aws.smithy.kotlin.runtime.net.url.Url
import com.fasterxml.jackson.databind.ObjectMapper
import dev.wezik.sandbox.domain.ProductEvent
import dev.wezik.sandbox.domain.event.BatchEventPublisher
import dev.wezik.sandbox.domain.event.BufferedEventPublisher
import dev.wezik.sandbox.domain.event.EventBuffer
import dev.wezik.sandbox.domain.event.EventPublisher
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
@EnableConfigurationProperties(ProductEventPublisherProperties::class)
class SnsConfig(private val productEventProperties: ProductEventPublisherProperties) {

  @Bean
  fun snsClient(): SnsClient = SnsClient {
    region = productEventProperties.region
    productEventProperties.endpointOverride?.takeIf { it.isNotBlank() }?.let {
      endpointUrl = Url.parse(it)
      // Use dummy credentials for LocalStack
      credentialsProvider = StaticCredentialsProvider(
        Credentials("test", "test")
      )
    }
  }

  @Bean(initMethod = "start", destroyMethod = "shutdown")
  fun productEventBuffer(
    batchPublisher: BatchEventPublisher<ProductEvent>,
    objectMapper: ObjectMapper,
  ): EventBuffer<ProductEvent> = EventBuffer(
    maxBatchCount = productEventProperties.maxBatchCount,
    maxBatchBytes = productEventProperties.maxBatchBytes,
    flushIntervalMs = productEventProperties.flushIntervalMs,
    estimateSize = { event -> objectMapper.writeValueAsBytes(event.toSnsMessage()).size },
    onFlush = { events -> batchPublisher.publishBatch(events) },
  )

  @Bean
  fun productEventPublisher(buffer: EventBuffer<ProductEvent>): EventPublisher<ProductEvent> =
    BufferedEventPublisher(buffer)
}

private class StaticCredentialsProvider(private val credentials: Credentials) : CredentialsProvider {
  override suspend fun resolve(attributes: Attributes): Credentials = credentials
}
