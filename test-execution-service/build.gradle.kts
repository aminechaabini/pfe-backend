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

    // HTTP Client for executing API tests
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // JSON Processing and Assertions
    implementation("net.javacrumbs.json-unit:json-unit:5.0.0")
    implementation("net.javacrumbs.json-unit:json-unit-assertj:5.0.0")
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")

    // XML Processing (for SOAP tests)
    implementation("xom:xom:1.3.9")
}
