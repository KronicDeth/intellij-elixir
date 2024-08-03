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

tasks {
    buildPlugin {
        enabled = false
    }

    buildSearchableOptions {
        enabled = false
    }

    composedJar {
        enabled = false
    }

    generateManifest {
        enabled = false
    }

    initializeIntellijPlatformPlugin {
        enabled = false
    }

    instrumentCode {
        enabled = false
    }

    instrumentedJar {
        enabled = false
    }

    instrumentTestCode {
        enabled = false
    }

    jarSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        enabled = false
    }

    prepareSandbox {
        enabled = false
    }

    prepareTest {
        enabled = false
    }

    prepareTestIdePerformanceSandbox {
        enabled = false
    }

    prepareTestIdeUiSandbox {
        enabled = false
    }

    prepareTestSandbox {
        enabled = false
    }

    printBundledPlugins {
        enabled = false
    }

    printProductsReleases {
        enabled = false
    }

    publishPlugin {
        enabled = false
    }

    runIde {
        enabled = false
    }

    setupDependencies {
        enabled = false
    }

    signPlugin {
        enabled = false
    }

    testIdePerformance {
        enabled = false
    }

    testIdeUi {
        enabled = false
    }

    verifyPlugin {
        enabled = false
    }

    verifyPluginProjectConfiguration {
        enabled = false
    }

    verifyPluginSignature {
        enabled = false
    }

    verifyPluginStructure {
        enabled = false
    }
}