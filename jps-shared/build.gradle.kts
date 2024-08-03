import org.gradle.api.tasks.testing.Test

plugins {
    id("java")
    id("org.jetbrains.intellij.platform.module")
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