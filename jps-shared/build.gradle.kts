import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.test.logger)
    id("org.jetbrains.intellij.platform.base")
}
base {
    archivesName.set("${rootProject.name}.${project.name}")
}

tasks.testClasses {
    enabled = false
}

// Restricts stdlib references to what 2025.3 (minimumSupported) bundles - the root project's
// own copy of this (build.gradle.kts) doesn't reach this separate project.
tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_2_2
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
// Java level and Kotlin toolchain are configured by the root build script, derived from
// the target platform (Java 25 for build 262+, otherwise the catalog's java version).
