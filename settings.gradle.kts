include("jps-shared", "jps-builder")

// Use IntelliJ Platform Gradle Plugin snapshot repository
pluginManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://www.jetbrains.com/intellij-repository/snapshots/")
        gradlePluginPortal()

    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Elixir"
