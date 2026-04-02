package dev.wezik.sandbox.infrastructure

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import javax.sql.DataSource

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
class DatabaseConfig {

  @Bean
  fun dslContext(dataSource: DataSource): DSLContext =
    DSL.using(dataSource, SQLDialect.POSTGRES)

  @Bean
  fun lockProvider(dataSource: DataSource): LockProvider {
    // Create shedlock table
    JdbcTemplate(dataSource).execute("""
      CREATE TABLE IF NOT EXISTS shedlock (
        name VARCHAR(64) NOT NULL PRIMARY KEY,
        lock_until TIMESTAMP NOT NULL,
        locked_at TIMESTAMP NOT NULL,
        locked_by VARCHAR(255) NOT NULL
      )
    """)

    return JdbcTemplateLockProvider(
      JdbcTemplateLockProvider.Configuration.builder()
        .withJdbcTemplate(JdbcTemplate(dataSource))
        .usingDbTime()
        .build()
    )
  }

  @Bean
  fun postgresProductRepository(
    dsl: DSLContext,
    objectMapper: com.fasterxml.jackson.databind.ObjectMapper,
  ): PostgresProductRepository = PostgresProductRepository(dsl, objectMapper).also { it.initialize() }
}
