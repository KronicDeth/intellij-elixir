import org.gradle.api.tasks.testing.Test

plugins {
    id("java")
    id("org.jetbrains.intellij.platform")
}

tasks.named<Jar>("jar") {
    archiveFileName.set("jps-shared.jar")
}

// Disable the test task instead of testClasses
tasks.named<Test>("test") {
    enabled = false
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
}

tasks.named("publishPlugin") {
    enabled = false
}

tasks.named("verifyPlugin") {
    enabled = false
}

tasks {
    buildSearchableOptions {
        enabled = false
    }
    verifyPlugin {
        enabled = false
    }
    publishPlugin {
        enabled = false
    }
}