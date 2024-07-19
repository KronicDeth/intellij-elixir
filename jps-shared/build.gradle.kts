import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    id("java")
}

tasks.named<Jar>("jar") {
    archiveFileName.set("jps-shared.jar")
}

tasks.named("testClasses") {
    enabled = false
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
}