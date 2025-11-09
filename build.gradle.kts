plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    java
}

java {
    // Use a safe LTS JDK for Spring Boot 3.x
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("net.javacrumbs.json-unit:json-unit:5.0.0")
    implementation("org.assertj:assertj-core:3.26.0")
    implementation("net.javacrumbs.json-unit:json-unit-assertj:5.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
        // Database - H2 for embedded, file-based storage
    runtimeOnly("com.h2database:h2")
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")
    implementation("xom:xom:1.3.9")
    implementation("dev.langchain4j:langchain4j-open-ai:1.8.0")
    implementation("dev.langchain4j:langchain4j:1.8.0")
}

tasks.test { useJUnitPlatform() }
