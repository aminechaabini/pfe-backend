rootProject.name = "demo"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Allow Gradle to auto-download a matching JDK for toolchains
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include("ai-runner")
include("test-execution-runner")
include("test-execution-runner")