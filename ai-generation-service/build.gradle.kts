plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Inter-module dependencies
    implementation(project(":common"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // AI/ML - LangChain4J for language model integration
    implementation("dev.langchain4j:langchain4j:1.8.0")
    implementation("dev.langchain4j:langchain4j-open-ai:1.8.0")

    // JSON Processing (for working with API specs and LLM responses)
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")

    // API Parsing (for understanding specs)
    implementation("io.swagger.parser.v3:swagger-parser:2.1.35")
    implementation("wsdl4j:wsdl4j:1.6.3")
}
