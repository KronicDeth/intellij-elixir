/*
 * Main Build Script
 * Purpose: Orchestrates the build of the IntelliJ Elixir plugin.
 * Key Changes for 2025.3 / Small IDE Support:
 * 1. Uses Version Catalog (libs.*) for dependency management.
 * 2. Explicitly configure JPS subprojects with TestFramework dependencies.
 * 3. QuoterService (BuildService) manages the Quoter daemon lifecycle with guaranteed cleanup.
 * 4. `test` task enforces the working directory to project root to find testData.
 */

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.intellij.platform.gradle.Constants
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.text.SimpleDateFormat
import java.util.*

// Uses the Version Catalog defined in gradle/libs.versions.toml
plugins {
    alias(libs.plugins.intellij.platform)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.download)
    alias(libs.plugins.test.logger)
    id("java")
    id("idea")
}

project.configurations.all {
    exclude(Constants.Configurations.Dependencies.BUNDLED_MODULE_GROUP, "com.intellij.kubernetes")
    exclude(Constants.Configurations.Dependencies.BUNDLED_PLUGIN_GROUP, "com.intellij.kubernetes")
}

// --- Version Catalog Captures ---
// Capture these early to avoid "Extension 'libs' not found" errors in subproject blocks
val javaVersionStr: String = libs.versions.java.get()
val libJunit = libs.junit
val libOpentest4j = libs.opentest4j
val libCommonsIo = libs.commons.io
val libMockitoCore = libs.mockito.core

// --- Configuration Properties ---
val elixirVersion: String by project
val quoterVersion: String by project

// Publish channel: "default" for release, "canary" for pre-release
val publishChannel: String = providers.gradleProperty("publishChannels").getOrElse("canary")

// Calculate the plugin version, for canary builds, the patch is bumped to the next version
// so that the IDE's plugin manager doesn't offer to "update" it to the released version.
val basePluginVersion: String = PluginVersion.getBaseVersion(
    providers.gradleProperty("pluginVersion").get(),
    publishChannel
)
val useDynamicEapVersion: Boolean = project.property("useDynamicEapVersion").toString().toBoolean()
val skipSearchableOptions: Boolean = project.property("skipSearchableOptions").toString().toBoolean()


val actualPlatformVersion: String = if (useDynamicEapVersion) {
    // Calling the helper from buildSrc
    VersionFetcher.getLatestEapBuild()
} else {
    project.property("platformVersion").toString()
}

// Setup Paths
val cachePath = layout.projectDirectory.dir("cache")
val elixirPath = cachePath.dir("elixir-$elixirVersion")
val quoterUnzippedPath = cachePath.dir("elixir-$elixirVersion-intellij_elixir-$quoterVersion")
val quoterExe = quoterUnzippedPath.file("_build/dev/rel/intellij_elixir/bin/intellij_elixir")
val quoterTmpPath = cachePath.dir("quoter_tmp_$quoterVersion")

// EXPORT FOR SUBPROJECTS (Required for jps-builder to access this path)
extra["elixirPath"] = elixirPath.asFile.absolutePath

// Version suffix logic:
// - "default" channel = no suffix (release build)
// - explicit versionSuffix property = use that
// - otherwise = "-pre+<timestamp>" (canary build)
val versionSuffix: String = when {
    publishChannel == "default" -> ""
    providers.gradleProperty("versionSuffix").isPresent &&
        providers.gradleProperty("versionSuffix").get().isNotEmpty() ->
            "-${providers.gradleProperty("versionSuffix").get()}"
    else -> "-pre+" + SimpleDateFormat("yyyyMMddHHmmss").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())
}

version = "$basePluginVersion$versionSuffix"

println("[elixir-build] platform=$actualPlatformVersion version=$version channel=$publishChannel dynamicEap=$useDynamicEapVersion skipSearchableOptions=$skipSearchableOptions quoterExe=$quoterExe quoterTmpPath=${quoterTmpPath.asFile.absolutePath}")

// --- Global Project Configuration ---
allprojects {
    apply(plugin = "java")
    apply(plugin = "com.adarshr.test-logger")

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(libJunit)
        testImplementation(libOpentest4j)
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersionStr")
        targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersionStr")
    }

    tasks.withType<JavaCompile> { options.encoding = "UTF-8" }

    configure<TestLoggerExtension> {
        theme = ThemeType.MOCHA
        showExceptions = true
        showStackTraces = true
        showFullStackTraces = false
        slowThreshold = 2000
        showSummary = true
        showStandardStreams = false
        showFailedStandardStreams = true
    }
}

// --- Subprojects (JPS) ---
subprojects {
    apply(plugin = "org.jetbrains.intellij.platform.module")

    repositories {
        intellijPlatform { defaultRepositories() }
    }

    dependencies {
        intellijPlatform {
            create(providers.gradleProperty("platformType"), providers.provider { actualPlatformVersion })
            bundledPlugins("com.intellij.java")
            testFramework(TestFrameworkType.Platform)
        }
        // JPS Builder tests extend UsefulTestCase (JUnit 3/4 style) and need explicit JUnit 4 on classpath
        testImplementation(libJunit)
    }

    sourceSets {
        main {
            java.srcDirs("src")
            resources.srcDirs("resources")
        }
        test {
            java.srcDir("tests")
        }
    }
}

// --- Root Project Repositories ---
repositories {
    intellijPlatform { defaultRepositories() }
}

// --- Source Sets ---
sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDirs("resources")
    }
    test {
        java.srcDir("tests")
    }
}

// --- IntelliJ Platform Configuration ---
intellijPlatform {
    if (skipSearchableOptions) {
        buildSearchableOptions = false
    }

    pluginConfiguration {
        id = providers.gradleProperty("pluginGroup")
        name = providers.gradleProperty("pluginName")
        version = project.version.toString()

        val stripTag = { text: String, tag: String -> text.replace("<${tag}>", "").replace("</${tag}>", "") }
        val bodyInnerHTML = { path: String -> stripTag(stripTag(file(path).readText(), "html"), "body") }

        changeNotes = bodyInnerHTML("resources/META-INF/changelog.html")
        description = bodyInnerHTML("resources/META-INF/description.html")

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.provider { null }
        }
        vendor {
            name = providers.gradleProperty("vendorName")
            email = providers.gradleProperty("vendorEmail")
            url = providers.gradleProperty("pluginRepositoryUrl")
        }
    }

    publishing {
        token = providers.environmentVariable("JET_BRAINS_MARKETPLACE_TOKEN")
        channels = listOf(publishChannel)
    }

    pluginVerification {
        ides {
            select {
                types = providers.gradleProperty("pluginVerifierIdeTypes")
                    .get()
                    .split(",")
                    .map { IntelliJPlatformType.valueOf(it.trim()) }

                channels = providers.gradleProperty("pluginVerifierChannels")
                    .get()
                    .split(",")
                    .map { ProductRelease.Channel.valueOf(it.trim()) }

                sinceBuild = providers.gradleProperty("pluginVerifierVersion").get()
            }
        }
    }
}

// --- Kotlin Configuration ---
kotlin {
    jvmToolchain(javaVersionStr.toInt())
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.valueOf("JVM_$javaVersionStr")
        freeCompilerArgs.add("-Xjvm-default=all")
        apiVersion = KotlinVersion.KOTLIN_2_2
    }
}

// --- Mockito Agent Configuration (Root project only) ---
val mockitoAgent = configurations.create("mockitoAgent")

// --- Dependencies ---
dependencies {
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.provider { actualPlatformVersion })
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(",") })
        bundledModules(providers.gradleProperty("platformBundledModules").map { it.split(",") })
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    implementation(project(":jps-builder"))
    implementation(project(":jps-shared"))
    implementation(files("lib/OtpErlang.jar"))
    implementation(libCommonsIo)

    testImplementation(libMockitoCore)
    mockitoAgent(libMockitoCore) { isTransitive = false }
}

// --- Run IDE Configuration ---
tasks.withType<RunIdeTask>().configureEach {
    jvmArguments.addAll(
        "-Didea.debug.mode=true",
        "-Didea.is.internal=true",
        "-Dlog4j2.debug=true",
        "-Dlogger.org=TRACE",
        "-XX:+AllowEnhancedClassRedefinition",
        "-Didea.ProcessCanceledException=disabled"
    )

    systemProperty("idea.log.debug.categories", "org.elixir_lang")
    maxHeapSize = "7g"

    if (project.hasProperty("runIdeWorkingDirectory") && project.property("runIdeWorkingDirectory").toString().isNotEmpty()) {
        workingDir = file(project.property("runIdeWorkingDirectory").toString())
    }

    // Development SDK paths - allows devs to auto-configure SDKs when running the plugin
    // Usage: ./gradlew runIde -PrunIdeSdkErlangPath='/path/to/erlang' -PrunIdeSdkElixirPath='/path/to/elixir'
    if (project.hasProperty("runIdeSdkErlangPath")) {
        systemProperty("runIdeSdkErlangPath", project.property("runIdeSdkErlangPath").toString())
    }
    if (project.hasProperty("runIdeSdkElixirPath")) {
        systemProperty("runIdeSdkElixirPath", project.property("runIdeSdkElixirPath").toString())
    }

    // Dynamic plugin loading
    // Usage: -PrunIdeCompatiblePlugins="PsiViewer,com.google.ide-perf,org.jetbrains.action-tracker"
    val compatiblePluginsList = providers.gradleProperty("runIdeCompatiblePlugins")
        .getOrElse("")
        .let { if (it.isEmpty()) emptyList() else it.split(",") }
    if (compatiblePluginsList.isNotEmpty()) {
        dependencies {
            intellijPlatform { compatiblePlugins(compatiblePluginsList) }
        }
    }
}

// Register Platform-specific Run Tasks dynamically
val runIdePlatformsList = providers.gradleProperty("runIdePlatforms").get().split(",")
val enableEAP = providers.gradleProperty("enableEAPIDEs").get().toBoolean()

runIdePlatformsList.forEach { platform ->
    intellijPlatformTesting.runIde.register("run${platform}", Action {
        type = IntelliJPlatformType.valueOf(platform)
        version = providers.gradleProperty("platformVersion${platform}").get()
        prepareSandboxTask {
            sandboxDirectory = layout.buildDirectory.dir("${platform.lowercase()}-sandbox")
        }
    })

    if (enableEAP) {
        intellijPlatformTesting.runIde.register("run${platform}EAP", Action {
            type = IntelliJPlatformType.valueOf(platform)
            version = providers.gradleProperty("platformVersion${platform}EAP").get()
            useInstaller = false
            prepareSandboxTask {
                sandboxDirectory = layout.buildDirectory.dir("${platform.lowercase()}_eap-sandbox")
            }
        })
    }
}

// --- External Tools (Elixir & Quoter) ---
val downloadElixir by tasks.registering(Download::class) {
    src("https://github.com/elixir-lang/elixir/archive/v${elixirVersion}.zip")
    dest(cachePath.file("Elixir.${elixirVersion}.zip"))
    overwrite(false)
}

val unzipElixir by tasks.registering(Copy::class) {
    dependsOn(downloadElixir)
    from(zipTree(downloadElixir.get().dest))
    into(elixirPath.asFile)
    eachFile { relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray()) }
    include("elixir-${elixirVersion}/**")
}

val buildElixir by tasks.registering(Exec::class) {
    dependsOn(unzipElixir)
    workingDir(elixirPath.asFile)
    commandLine("make")
    outputs.dir(elixirPath.dir("bin"))
    // The apps that are part of Elixir SDK. `make` compiles them and puts output into `ebin` and `_build` dirs.
    // We need to declare them as outputs for Gradle's UP-TO-DATE checks to work correctly.
    // The list is based on the applications shipped with Elixir v1.13.4.
    val libDir = elixirPath.dir("lib")
    listOf("eex", "elixir", "ex_unit", "iex", "logger", "mix").forEach { appName ->
        val appDir = libDir.dir(appName)
        // All apps get an 'ebin' directory.
        outputs.dir(appDir.dir("ebin"))
        // All apps except 'elixir' itself also get a '_build' directory.
        if (appName != "elixir") {
            outputs.dir(appDir.dir("_build"))
        }
    }
}

val getElixir by tasks.registering {
    dependsOn(buildElixir)
}


val getQuoter by tasks.registering(Download::class) {
    src("https://github.com/KronicDeth/intellij_elixir/archive/v${quoterVersion}.zip")
    dest(cachePath.file("intellij_elixir-${quoterVersion}.zip"))
    overwrite(false)
}


val unzipQuoter by tasks.registering(Copy::class) {
    dependsOn(getQuoter)

    // 1. Target the final destination directly
    into(quoterUnzippedPath)

    from(zipTree(getQuoter.get().dest)) {
        // 2. Strip the top-level directory 'intellij_elixir-${quoterVersion}' on the fly
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }

        // 3. Prevent creating the empty top-level directory itself
        includeEmptyDirs = false
    }
}

val getQuoterDeps by tasks.registering(Exec::class) {
    dependsOn(unzipQuoter)
    workingDir(quoterUnzippedPath)

    // 1. INPUTS
    // mix.exs defines the requirements
    inputs.file(quoterUnzippedPath.file("mix.exs"))
        .withPropertyName("mixExs")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    // mix.lock defines the exact versions.
    // It is an INPUT because 'unzipQuoter' created it, and this task READS it.
    inputs.file(quoterUnzippedPath.file("mix.lock"))
        .withPropertyName("mixLock")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    // 2. OUTPUTS
    // This task produces/populates the 'deps' directory.
    outputs.dir(quoterUnzippedPath.dir("deps"))
        .withPropertyName("depsDir")

    // 3. CACHING
    outputs.cacheIf { true }

    commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get")
}

val releaseQuoter by tasks.registering(Exec::class) {
    dependsOn(getQuoterDeps)

    // 1. Use Directory object directly (No .asFile)
    workingDir(quoterUnzippedPath)

    // 2. INPUTS: What determines if a release needs rebuilding?
    //    If mix.exs, lockfile, or the source code (lib/config) changes, re-run.
    inputs.files(
        quoterUnzippedPath.file("mix.exs"),
        quoterUnzippedPath.file("mix.lock")
    ).withPathSensitivity(PathSensitivity.RELATIVE)

    //    Add source directories so code changes trigger a rebuild
    inputs.dir(quoterUnzippedPath.dir("lib")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(quoterUnzippedPath.dir("config")).withPathSensitivity(PathSensitivity.RELATIVE)
    //    The dependencies (generated by previous task) are inputs for this task
    inputs.dir(quoterUnzippedPath.dir("deps")).withPathSensitivity(PathSensitivity.RELATIVE)

    // 3. OUTPUTS: This replaces your 'onlyIf' check.
    //    If this file exists and inputs match, Gradle skips this task automatically.
    outputs.dir(quoterUnzippedPath.dir("_build"))
        .withPropertyName("buildDir")

    commandLine("mix", "release")
}

// Register the QuoterService - Gradle calls close() at build end regardless of failure
// See: https://docs.gradle.org/current/userguide/build_services.html
val quoterService = gradle.sharedServices.registerIfAbsent("quoter", QuoterService::class) {
    parameters {
        executable.set(quoterExe)
        tmpDir.set(quoterTmpPath)
    }
}

val startQuoter by tasks.registering(StartQuoterTask::class) {
    dependsOn(releaseQuoter)
}

// --- Test Configuration ---

// ALL test tasks in ALL projects use the QuoterService (ensures cleanup on any failure)
allprojects {
    tasks.withType<Test>().configureEach {
        dependsOn(startQuoter)
        usesService(quoterService)
    }
}

tasks.named<Test>("test") {
    dependsOn("prepareTestSandbox")

    environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath.asFile.absolutePath)
    environment("ELIXIR_EBIN_DIRECTORY", elixirPath.dir("lib/elixir/ebin/").asFile.absolutePath + File.separator)
    environment("ELIXIR_VERSION", elixirVersion)

    // Add Mockito as javaagent to avoid dynamic loading warnings (root project only)
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.named<Zip>("buildPlugin") {
    doLast {
        println("Note: Timestamps in version strings and filenames of build artifacts do not change on every build due to gradle config caching.")
        println("Built artifact path: ${archiveFile.get().asFile.absolutePath}")
    }
}

// Uncomment to allow using build-scan.
//if (hasProperty("buildScan")) {
//    extensions.findByName("buildScan")?.withGroovyBuilder {
//        setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
//        setProperty("termsOfServiceAgree", "yes")
//    }
//}
