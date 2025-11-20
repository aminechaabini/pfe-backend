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
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")  // Web application support
    implementation("org.springframework.boot:spring-boot-starter-validation")  // Bean validation
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")  // JPA support
    testImplementation("org.springframework.boot:spring-boot-starter-test")  // Testing support
    
    // Jakarta EE APIs
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")  // JPA API
    
    // JSON Processing
    implementation("net.javacrumbs.json-unit:json-unit:5.0.0")  // JSON assertion library
    implementation("net.javacrumbs.json-unit:json-unit-assertj:5.0.0")  // AssertJ support for JSON assertions
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")  // JSON processing
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")  // Lightweight JSON library
    
    // Testing
    implementation("org.assertj:assertj-core:3.26.0")  // Fluent assertions
    
    // Database - H2 for embedded, file-based storage
    runtimeOnly("com.h2database:h2")
    
    // XML Processing
    implementation("xom:xom:1.3.9")  // XML object model
    
    // AI/ML - LangChain4J for language model integration
    implementation("dev.langchain4j:langchain4j:1.8.0")  // Core LangChain4J
    implementation("dev.langchain4j:langchain4j-open-ai:1.8.0")  // OpenAI integration

    // MapStruct for object mapping
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // API Documentation
    implementation("io.swagger.parser.v3:swagger-parser:2.1.35")  // OpenAPI/Swagger parsing
    implementation("wsdl4j:wsdl4j:1.6.3")  // WSDL parsing for SOAP services
}

sourceSets {
    main {
        java {
            exclude("com/example/demo/orchestrator/api")
            exclude("com/example/demo/orchestrator/app")
            exclude("com/example/demo/llm_adapter")
            exclude("com/example/demo/Runner")
            exclude("com/example/demo/orchestrator/dto")
            exclude("com/example/demo/orchestrator/infrastructure/spec")
        }
    }
}

tasks.test { useJUnitPlatform() }
