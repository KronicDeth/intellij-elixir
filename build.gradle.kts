import de.undercouch.gradle.tasks.download.Download
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

// Helper functions for accessing environment variables
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    id("jacoco")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.gradleDownloadTask)
}

// Project configuration
group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// Define paths and versions
val cachePath = "${rootDir}/cache"
val elixirVersion = providers.gradleProperty("elixirVersion").get()
val elixirPath = "${cachePath}/elixir-${elixirVersion}"
val quoterVersion = "2.1.0"
val quoterUnzippedPathValue = "${cachePath}/elixir-${elixirVersion}-intellij_elixir-${quoterVersion}"
val quoterReleasePathValue = "${quoterUnzippedPathValue}/intellij_elixir-${quoterVersion}/_build/dev/rel/intellij_elixir"
val tmpDirPath = "${rootDir}/cachetmp"
val quoterExe = "${quoterReleasePathValue}/bin/intellij_elixir"
val quoterZipPathValue = "${cachePath}/intellij_elixir-${quoterVersion}.zip"

// Version suffix and channel configuration
val versionSuffix = if (project.hasProperty("isRelease") && project.property("isRelease") == "true") {
    ""
} else {
    val buildTime: String = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    "-pre+${buildTime}"
}
val channel = if (project.hasProperty("isRelease") && project.property("isRelease") == "true") "default" else "canary"

version = "${providers.gradleProperty("pluginVersion").get()}$versionSuffix"

// Repository configuration
repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Dependencies
dependencies {
    implementation(project(":jps-builder"))
    implementation(project(":jps-shared"))
    implementation(files("lib/OtpErlang.jar"))
    implementation(libs.annotations)
    implementation(libs.commonsIo)
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.mockito)
    testImplementation(libs.objenesis)
    testImplementation("org.opentest4j:opentest4j:1.3.0")

    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))
        // intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
        instrumentationTools()
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        testFramework(TestFrameworkType.Platform)
        pluginVerifier()
        zipSigner()
    }
}

// Kotlin configuration
kotlin {
    jvmToolchain(17)
}

// Source sets configuration
sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("tests")
    }
}

// IDEA project configuration
idea {
    project {
        jdkName = providers.gradleProperty("javaVersion").get()
    }
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

// IntelliJ Platform plugin configuration
intellijPlatform {
    pluginConfiguration {
        id = providers.gradleProperty("pluginGroup")
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")
        description = providers.gradleProperty("pluginDescription")
        changeNotes = providers.gradleProperty("pluginChangeNotes")
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
        vendor {
            name = providers.gradleProperty("vendorName")
            email = providers.gradleProperty("vendorEmail")
            url = providers.gradleProperty("pluginRepositoryUrl")
        }
    }

    signing {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = environment("PUBLISH_TOKEN")
        channels = providers.gradleProperty("pluginVersion").map {
            listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }
}

// Changelog configuration
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

val compilationPackages = listOf("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")

// Java configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// Task configuration
tasks {
    // Kotlin compilation
    compileKotlin {
        kotlinOptions.jvmTarget = providers.gradleProperty("javaVersion").get()
        kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    // Cache and build cleanup
    register<Delete>("deleteCache") {
        delete("cache")
        delete("build")
        delete("jps-shared/build")
        delete("jps-builder/build")
    }

    clean {
        dependsOn("deleteCache")
    }

    // Java compilation
    compileJava {
        dependsOn(":jps-builder:composedJar", ":jps-shared:composedJar")
        sourceCompatibility = providers.gradleProperty("javaVersion").get()
        targetCompatibility = providers.gradleProperty("javaVersion").get()
    }
    named("compileTestJava") {
        dependsOn(":jps-builder:composedJar", ":jps-shared:composedJar", "makeElixir")
    }

    // IDE run configuration
    runIde {
        systemProperty("idea.log.info.categories", "org.intellij_lang=TRACE")
        jvmArgs(
            "-Didea.info.mode=true",
            "-Didea.is.internal=true",
            "-Dlog4j2.info=true",
            "-Dlogger.org=TRACE",
            "idea.ProcessCanceledException=disabled"
        )
    }

    // Gradle wrapper
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    // UI testing configuration
    testIdeUi {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

    // Test configuration
    test {
        environment("RELEASE_TMP", tmpDirPath)
        environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
        environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
        environment("ELIXIR_VERSION", elixirVersion)
        isScanForTestClasses = false
        include("**/Issue*.class")
        include("**/*Test.class")
        include("**/*TestCase.class")
        dependsOn(":jps-builder:composedJar", ":jps-shared:composedJar")

        useJUnit()
        exclude(compilationPackages)
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    // Compilation test configuration
    register<Test>("testCompilation") {
        group = "Verification"
        dependsOn("classes", "testClasses")
        environment("RELEASE_TMP", tmpDirPath)
        useJUnit {
            include(compilationPackages)
        }
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    // Elixir download and setup
    register<Download>("downloadElixir") {
        src("https://github.com/elixir-lang/elixir/archive/v$elixirVersion.zip")
        dest("$rootDir/cache/Elixir.$elixirVersion.zip")
        overwrite(false)

        inputs.property("elixirVersion", elixirVersion)
        outputs.file(dest)
        outputs.cacheIf { true }
    }

    register<Copy>("extractElixir") {
        dependsOn("downloadElixir")
        from(zipTree("$rootDir/cache/Elixir.$elixirVersion.zip"))
        into("$rootDir/cache/")
        outputs.dir(elixirPath)
    }

    register<Exec>("makeElixir") {
        dependsOn("extractElixir")
        workingDir = file(elixirPath)
        commandLine("make")
        logging.captureStandardOutput(LogLevel.LIFECYCLE)
        logging.captureStandardError(LogLevel.ERROR)
        inputs.dir(elixirPath)
        outputs.dir("$elixirPath/bin")
        outputs.cacheIf { true }
    }

    // Quoter setup
    register<Download>("downloadQuoter") {
        src("https://github.com/KronicDeth/intellij_elixir/archive/v$quoterVersion.zip")
        dest(quoterZipPathValue)
        overwrite(false)
        onlyIfModified(true)
        inputs.property("quoterVersion", quoterVersion)
        outputs.file(quoterZipPathValue)
        outputs.cacheIf { true }
    }

    register<Copy>("extractQuoter") {
        dependsOn("downloadQuoter", "extractElixir")
        from(zipTree(quoterZipPathValue))
        into(quoterUnzippedPathValue)
        inputs.file(quoterZipPathValue)
        outputs.dir(quoterUnzippedPathValue)
        outputs.cacheIf { true }
    }

    register<ReleaseQuoterTask>("releaseQuoter") {
        dependsOn("extractQuoter")
        quoterUnzippedPath.set(layout.dir(provider { file("${quoterUnzippedPathValue}/intellij_elixir-${quoterVersion}") }))
        releaseOutputDir.set(layout.dir(provider { file(quoterReleasePathValue) }))
    }

    // Quoter daemon management
    register<Exec>("runQuoter") {
        dependsOn("releaseQuoter")
        environment("RELEASE_TMP", tmpDirPath)
        environment("RELEASE_COOKIE", "intellij_elixir")
        environment("RELEASE_DISTRIBUTION", "name")
        environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
        workingDir = file(quoterReleasePathValue)
        commandLine = listOf(quoterExe, "daemon")
        logging.captureStandardOutput(LogLevel.LIFECYCLE)
        logging.captureStandardError(LogLevel.ERROR)
    }

    register<Exec>("stopQuoter") {
        dependsOn("releaseQuoter")
        environment("RELEASE_TMP", tmpDirPath)
        environment("RELEASE_COOKIE", "intellij_elixir")
        environment("RELEASE_DISTRIBUTION", "name")
        environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
        isIgnoreExitValue = true
        workingDir = file(quoterReleasePathValue)
        commandLine = listOf(quoterExe, "stop")
    }

    test {
        dependsOn("runQuoter", "makeElixir")
        finalizedBy("stopQuoter")
        environment("RELEASE_TMP", tmpDirPath)
    }
}

// Configuration for all projects
allprojects {
    apply(plugin = "java")
    extra["elixirPath"] = elixirPath

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
project(":") {
    apply(plugin = "kotlin")
}
// Configuration for subprojects
subprojects {
    apply(plugin = "org.jetbrains.intellij.platform.module")

    extra["elixirPath"] = elixirPath

    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
            jetbrainsRuntime()
            mavenCentral()
        }
    }
    dependencies {
        testImplementation("junit:junit:4.13.2")
        testImplementation("org.opentest4j:opentest4j:1.3.0")
        intellijPlatform {
            create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))
            instrumentationTools()
            bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })
            plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
            testFramework(TestFrameworkType.Platform)
            pluginVerifier()
            zipSigner()
        }
    }

    tasks.named("compileTestJava") {
        dependsOn(":makeElixir")
    }

    tasks.test {
        dependsOn(":runQuoter", ":makeElixir")
        finalizedBy(":stopQuoter")

        environment("ELIXIR_LANG_ELIXIR_PATH", rootProject.extra["elixirPath"] as String)
        environment("ELIXIR_EBIN_DIRECTORY", "${rootProject.extra["elixirPath"]}/lib/elixir/ebin/")
        environment("ELIXIR_VERSION", rootProject.extra["elixirVersion"] as String)

        useJUnit()
        exclude(compilationPackages)

        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        environment("RELEASE_TMP", tmpDirPath)
    }
}

// Configuration for jps-builder project
project(":jps-builder") {
    apply(plugin = "java")

    dependencies {
        implementation(project(":jps-shared"))
    }
}