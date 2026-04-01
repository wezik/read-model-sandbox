package dev.wezik.sandbox.infrastructure.event

import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.MessageAttributeValue
import aws.sdk.kotlin.services.sns.model.PublishRequest
import com.fasterxml.jackson.databind.ObjectMapper
import dev.wezik.sandbox.domain.event.BatchEventPublisher
import dev.wezik.sandbox.domain.ProductEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SnsBatchProductEventPublisher(
  private val snsClient: SnsClient,
  private val properties: ProductEventPublisherProperties,
  private val objectMapper: ObjectMapper,
) : BatchEventPublisher<ProductEvent> {

  private val logger = KotlinLogging.logger {}

  override suspend fun publishBatch(events: List<ProductEvent>) {
    if (events.isEmpty()) return

    val key = events.first().key
    val messages = events.map { it.toSnsMessage() }
    val payload = objectMapper.writeValueAsString(messages)
    val messageGroupId = events.first().product.id.toString()
    val deduplicationId = UUID.randomUUID().toString()

    val request = PublishRequest {
      topicArn = properties.topicArn
      message = payload
      this.messageGroupId = messageGroupId
      this.messageDeduplicationId = deduplicationId
      messageAttributes = mapOf(
        "eventType" to MessageAttributeValue {
          dataType = "String"
          stringValue = key
        },
        "batchSize" to MessageAttributeValue {
          dataType = "Number"
          stringValue = events.size.toString()
        }
      )
    }

    try {
      val response = snsClient.publish(request)
      logger.info { "Published batch of ${events.size} $key events, messageId: ${response.messageId}" }
    } catch (e: Exception) {
      logger.error(e) { "Failed to publish $key events batch" }
      throw e
    }
  }
}
