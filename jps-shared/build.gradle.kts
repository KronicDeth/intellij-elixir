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

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
// Java level and Kotlin toolchain are configured by the root build script, derived from
// the target platform (Java 25 for build 262+, otherwise the catalog's java version).
