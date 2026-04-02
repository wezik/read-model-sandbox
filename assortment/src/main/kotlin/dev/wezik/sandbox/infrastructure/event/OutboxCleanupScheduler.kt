package dev.wezik.sandbox.infrastructure.event

import dev.wezik.sandbox.domain.ProductEvent
import dev.wezik.sandbox.domain.event.Outbox
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@Component
class OutboxCleanupScheduler(
  private val outbox: Outbox<ProductEvent>,
  private val properties: ProductEventPublisherProperties,
) {
  private val logger = KotlinLogging.logger {}

  @Scheduled(fixedRateString = "\${events.outbox.cleanup-interval-ms:3600000}")
  @SchedulerLock(name = "outbox-cleanup", lockAtLeastFor = "5m", lockAtMostFor = "30m")
  fun cleanup() {
    val retention = Duration.ofHours(properties.outbox.retentionHours)
    val cutoff = Instant.now().minus(retention)
    logger.info { "Running outbox cleanup, deleting entries published before $cutoff" }
    runBlocking {
      outbox.deletePublishedBefore(cutoff)
    }
  }
}
