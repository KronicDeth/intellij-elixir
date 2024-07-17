import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    id("java")
    alias(libs.plugins.intelliJPlatform)
}

tasks.named<Jar>("jar") {
    archiveFileName.set("jps-shared.jar")
}

tasks.named("testClasses") {
    enabled = false
}
repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("241.18034.62") // Use the same version as the root project
    }
}
