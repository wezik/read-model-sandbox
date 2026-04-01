import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  // Gradle behavior
  id("io.spring.dependency-management") version "1.1.7"
  id("com.dorongold.task-tree") version "4.0.1"
  id("com.adarshr.test-logger") version "4.0.0"

  // Application
  kotlin("jvm") version "2.3.20"
  kotlin("plugin.spring") version "2.3.20"
  id("org.springframework.boot") version "4.0.3"

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

    // Tests
    dependency("io.mockk:mockk:1.14.9")
    dependency("io.kotest:kotest-runner-junit5:5.9.1")
    dependency("io.kotest:kotest-framework-datatest:5.9.1")
    dependency("io.kotest:kotest-assertions-json:5.9.1")
    dependency("io.kotest.extensions:kotest-assertions-arrow:2.0.0")
    dependency("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    dependency("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
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
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-webclient")
  implementation("org.springframework.data:spring-data-commons")

  // Testing
  testImplementation("io.mockk:mockk")
  testImplementation("io.kotest:kotest-runner-junit5")
  testImplementation("io.kotest:kotest-framework-datatest")
  testImplementation("io.kotest:kotest-assertions-json")
  testImplementation("io.kotest.extensions:kotest-extensions-spring")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("io.kotest.extensions:kotest-assertions-arrow")
  testImplementation("io.kotest.extensions:kotest-extensions-testcontainers")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.assertj")
    exclude(group = "org.awaitility")
    exclude(group = "org.mockito")
  }
  testImplementation("org.springframework.modulith:spring-modulith-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")

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
      "-Xjsr305=strict",
      "-Xemit-jvm-type-annotations",
    )
  }
}

// Spring Boot

group = "dev.wezik.sandbox"
version = "0.0.1-SNAPSHOT"

springBoot {
  buildInfo()
}

tasks.bootJar {
  archiveFileName = "marketplace-app.jar"
}

// Tests and code coverage

tasks.test {
  onlyIf { !properties.contains("skipTests") }

  useJUnitPlatform()
  maxHeapSize = "768m"
}
