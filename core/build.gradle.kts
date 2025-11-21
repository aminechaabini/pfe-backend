plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Inter-module dependencies
    implementation(project(":shared-contracts"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Jakarta EE APIs
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Database - H2 for embedded, file-based storage
    runtimeOnly("com.h2database:h2")

    // API Documentation and Parsing
    implementation("io.swagger.parser.v3:swagger-parser:2.1.35")
    implementation("wsdl4j:wsdl4j:1.6.3")

    // XML Processing
    implementation("xom:xom:1.3.9")

    // JSON Processing
    implementation("net.javacrumbs.json-unit:json-unit:5.0.0")
    implementation("net.javacrumbs.json-unit:json-unit-assertj:5.0.0")
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")

    // HTTP Client for communicating with other services
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

sourceSets {
    main {
        java {
            exclude("com/example/demo/core/api")
            exclude("com/example/demo/core/app")
            exclude("com/example/demo/core/dto")
            exclude("com/example/demo/core/infrastructure/spec")
            exclude("com/example/demo/core/application/ports")
        }
    }
}
