//import de.undercouch.gradle.tasks.download.Download
//import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


plugins {
    id("java")
    alias(libs.plugins.kotlin)
//    alias(libs.plugins.intelliJPlatform)
//    alias(libs.plugins.changelog)
//    alias(libs.plugins.qodana)
//    alias(libs.plugins.kover)
//    alias(libs.plugins.gradleDownloadTask)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

