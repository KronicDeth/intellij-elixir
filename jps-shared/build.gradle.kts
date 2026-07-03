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
kotlin {
    jvmToolchain(21)
}
