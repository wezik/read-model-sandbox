// import dev.monosoul.jooq.RecommendedVersions
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
// import org.jooq.meta.jaxb.ForcedType
// import org.jooq.meta.jaxb.SchemaMappingType
// import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
  // Gradle behavior
  id("io.spring.dependency-management") version "1.1.7"
  id("com.dorongold.task-tree") version "4.0.1"
  id("com.adarshr.test-logger") version "4.0.0"

  // Application
  kotlin("jvm") version "2.3.20"
  kotlin("plugin.spring") version "2.3.20"
  id("org.springframework.boot") version "4.0.3"
  // id("dev.monosoul.jooq-docker") version "8.0.15"

  // Code style and formatting
  id("com.diffplug.spotless") version "8.3.0"
}

// Gradle wrapper

tasks.wrapper {
  gradleVersion = "9.4.0"
}

// Dependencies

repositories {
  maven {
    url = uri("https://nexus.tradebyte.org/repository/maven-public/")
  }
  mavenCentral()
}

dependencyManagement {

  imports {
    mavenBom("org.springframework.modulith:spring-modulith-bom:2.0.3")
    mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2")
    mavenBom("aws.sdk.kotlin:bom:1.6.36")
  }

  dependencies {

    // Kotlin & standard library
    dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")
    dependency("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.10.2")

    // Utilities
    dependency("io.arrow-kt:arrow-core:2.2.2")
    dependency("io.github.oshai:kotlin-logging-jvm:8.0.01")
    dependency("org.joda:joda-money:2.0.3")

    // Persistence
    // project.extra["jooq.version"] = RecommendedVersions.JOOQ_VERSION
    // project.extra["flyway.version"] = RecommendedVersions.FLYWAY_VERSION
    // dependency("org.jooq:jooq-kotlin-coroutines:${project.extra["jooq.version"]}")
    // dependency("org.jooq:jooq-jackson-extensions:${project.extra["jooq.version"]}")

    // Web
    dependency("com.fasterxml.jackson.datatype:jackson-datatype-joda-money:2.21.1")
    dependency("io.ktor:ktor-serialization-jackson:3.4.1")

    // Tests
    dependency("io.mockk:mockk:1.14.9")
    // dependency("com.ninja-squad:springmockk:5.0.1")
    dependency("io.kotest:kotest-runner-junit5:5.9.1")
    dependency("io.kotest:kotest-framework-datatest:5.9.1")
    dependency("io.kotest:kotest-assertions-json:5.9.1")
    dependency("io.kotest.extensions:kotest-assertions-arrow:2.0.0")
    dependency("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    dependency("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    // dependency("org.wiremock:wiremock-standalone:3.13.2")
    // dependency("org.testcontainers:postgresql:1.21.4")
    // dependency("org.testcontainers:r2dbc:1.21.4")
    // dependency("org.testcontainers:localstack:1.21.4")
  }

}


dependencies {
  // Kotlin & standard library
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")

  // Utilities
  implementation("io.arrow-kt:arrow-core")
  implementation("io.github.oshai:kotlin-logging-jvm")
  implementation("org.joda:joda-money")

  // Modulith
  implementation("org.springframework.modulith:spring-modulith-starter-core")

  // Web
  // implementation("org.springdoc:springdoc-openapi-starter-webflux-ui")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda-money")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  // Spring Boot 4 defaults to Jackson 3. AWS SDK still uses Jackson 2.
  implementation("org.springframework.boot:spring-boot-jackson2")
  implementation("org.springframework.data:spring-data-commons")
  implementation("io.ktor:ktor-serialization-jackson")

  // Persistence (R2DBC, Jooq)
  // implementation("org.springframework.boot:spring-boot-starter-r2dbc")
  // implementation("org.springframework.boot:spring-boot-starter-jooq")
  // implementation("org.jooq:jooq-kotlin")
  // implementation("org.jooq:jooq-kotlin-coroutines")
  // implementation("org.postgresql:r2dbc-postgresql")
  // runtimeOnly("io.r2dbc:r2dbc-pool")

  // JDBC for Flyway and JOOQ codegen
  // implementation("org.springframework.boot:spring-boot-starter-flyway")
  // runtimeOnly("org.flywaydb:flyway-database-postgresql")
  // runtimeOnly("org.postgresql:postgresql")
  // jooqCodegen("org.postgresql:postgresql")

  // AWS
  // implementation("aws.sdk.kotlin:s3")
  // implementation("aws.sdk.kotlin:sqs")
  implementation("aws.sdk.kotlin:sns")

  // Postgres + jOOQ for persistence
  implementation("org.postgresql:postgresql:42.7.7")
  implementation("org.jooq:jooq:3.20.4")
  implementation("org.jooq:jooq-kotlin:3.20.4")

  // ShedLock for distributed scheduling
  // Source: https://mvnrepository.com/artifact/net.javacrumbs.shedlock/shedlock-spring
  implementation("net.javacrumbs.shedlock:shedlock-spring:7.7.0")
  implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:7.7.0")

  // Testing
  testImplementation("io.mockk:mockk")
  // testImplementation("com.ninja-squad:springmockk")
  testImplementation("io.kotest:kotest-runner-junit5")
  testImplementation("io.kotest:kotest-framework-datatest")
  testImplementation("io.kotest:kotest-assertions-json")
  testImplementation("io.kotest.extensions:kotest-extensions-spring")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("io.kotest.extensions:kotest-assertions-arrow")
  testImplementation("io.kotest.extensions:kotest-extensions-testcontainers")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    // Don't pull in test dependencies from Java world which are already covered by Kotest
    exclude(group = "org.assertj")
    exclude(group = "org.awaitility")
    exclude(group = "org.mockito")
  }
  testImplementation("org.springframework.modulith:spring-modulith-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  // testImplementation("org.wiremock:wiremock-standalone")
  // testImplementation("org.testcontainers:postgresql")
  // testImplementation("org.testcontainers:r2dbc")
  // testImplementation("org.testcontainers:localstack")

  // Other
  developmentOnly("org.springframework.boot:spring-boot-devtools")
}

// Java compiler

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(23)
  }
}

// Kotlin compiler

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_23
    freeCompilerArgs = listOf(
      "-Xjsr305=strict", // Enable strict null checking
      "-Xemit-jvm-type-annotations", // Enable type annotations
    )
  }
}

// Spring Boot

group = "dev.wezik.sandbox"
version = "0.0.1-SNAPSHOT"

springBoot {
  // See: https://docs.spring.io/spring-boot/how-to/build.html#howto.build.generate-info
  buildInfo()
}

tasks.bootJar {
  archiveFileName = "assortment-app.jar"
}

// Tests and code coverage

tasks.test {
  // We want the CI process to first build the whole application and then run tests. Specifying `-X test` won't run
  // the tests but also won't compile the test classes (which we want). This trick allows disabling execution of
  // tests while maintaining the task graph (in particular, compiling test classes).
  onlyIf { !properties.contains("skipTests") }

  useJUnitPlatform()
  maxHeapSize = "768m"
  // finalizedBy(tasks.jacocoTestReport)
}
