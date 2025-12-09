/*
 * buildSrc Configuration
 * Purpose: Enables Kotlin DSL support for the code inside `buildSrc`.
 * This allows `VersionFetcher.kt` to be compiled and available to the main build script.
 */
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}
