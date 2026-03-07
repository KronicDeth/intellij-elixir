/*
 * Main Build Script
 * Purpose: Orchestrates the build of the IntelliJ Elixir plugin.
 *
 * Platform Support:
 * - POSIX (Linux, macOS): Full support
 * - Windows: Full support (Git Bash, MSYS2, WSL, or native)
 *
 * Key Components:
 * 1. ElixirService (BuildService): Manages Elixir installation and build
 * 2. QuoterService (BuildService): Manages Quoter daemon lifecycle with guaranteed cleanup
 * 3. Platform abstraction: Automatic detection and platform-specific implementations
 * 4. Tasks: Thin wrappers for CI caching and developer discoverability
 *
 * Version Catalog: Uses libs.* for dependency management (gradle/libs.versions.toml)
 * Configuration Cache: Fully compatible with Gradle configuration cache
 */

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import de.undercouch.gradle.tasks.download.Download
import deps.registerResolveExternalDependenciesTasksForAllProjects
import elixir.ElixirService
import org.jetbrains.intellij.platform.gradle.Constants
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import quoter.QuoterService
import quoter.tasks.GetQuoterDepsTask
import quoter.tasks.ReleaseQuoterTask
import quoter.tasks.StartQuoterTask
import versioning.PluginVersion
import versioning.VersionFetcher
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

// Calculate the plugin version; for canary builds, the patch is bumped to the next version
// so that the IDE's plugin manager doesn't offer to "update" it to the released version.
val basePluginVersion: String = PluginVersion.getBaseVersion(
    providers.gradleProperty("pluginVersion").get(),
    publishChannel
)

val useDynamicEapVersion: Boolean = project.property("useDynamicEapVersion").toString().toBoolean()
val skipSearchableOptions: Boolean = project.property("skipSearchableOptions").toString().toBoolean()

val actualPlatformVersion: String = if (useDynamicEapVersion) {
    // Calling the helper from buildSrc
    VersionFetcher.getLatestEapBuild(platformType = providers.gradleProperty("platformType").get() ?: "IU")
} else {
    project.property("platformVersion").toString()
}

// Setup Paths
val cachePath: Directory = layout.projectDirectory.dir("cache")
val elixirPath: Directory = cachePath.dir("elixir-$elixirVersion")
val quoterUnzippedPath: Directory = cachePath.dir("elixir-$elixirVersion-intellij_elixir-$quoterVersion")
val quoterExe: RegularFile = quoterUnzippedPath.file("_build/dev/rel/intellij_elixir/bin/intellij_elixir")
val quoterTmpPath: Directory = cachePath.dir("quoter_tmp_$quoterVersion")

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

logger.lifecycle("[elixir-build] platform=$actualPlatformVersion version=$version channel=$publishChannel dynamicEap=$useDynamicEapVersion skipSearchableOptions=$skipSearchableOptions quoterExe=$quoterExe quoterTmpPath=${quoterTmpPath.asFile.absolutePath}")

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

    tasks.withType<Test>().configureEach {
        inputs.dir(rootProject.layout.projectDirectory.dir("testData"))
            .withPropertyName("testData")
            .withPathSensitivity(PathSensitivity.RELATIVE)
    }
}

// --- Subprojects (JPS) ---
subprojects {
    apply(plugin = "org.jetbrains.intellij.platform.base")

    repositories {
        intellijPlatform { defaultRepositories() }
    }

    dependencies {
        intellijPlatform {
            create(providers.gradleProperty("platformType"), providers.provider { actualPlatformVersion })
        }
    }

    sourceSets {
        main {
            java.srcDirs("src")
            resources.srcDirs("resources")
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
    create("testUI", Action<SourceSet> {
        kotlin.srcDir("testUI/kotlin")
        resources.srcDir("testUI/resources")
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
    })
}

idea {
    module {
        testSources.from(sourceSets["testUI"].kotlin.srcDirs)
        testResources.from(sourceSets["testUI"].resources.srcDirs)
    }
}

val testUIImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val testUIRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

// --- IntelliJ Platform Configuration ---
intellijPlatform {
    if (skipSearchableOptions) {
        buildSearchableOptions = false
    }
    // Disable auto-reload by default: it is noisy during runIde and has been unreliable.
    // Re-enable via `ideaAutoReload=true` in `gradle.properties` or `-PideaAutoReload=true`.
    autoReload = providers.gradleProperty("ideaAutoReload")
        .map { it.toBoolean() }
        .orElse(false)

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
        // This allows us to verify small IDEs without errors about the JPS plugin.
        // It's not ideal, but there doesn't seem to be any other way to deal with it right now.
        externalPrefixes.add("org.jetbrains.jps")
    }
    sourceSets {
        val testUI: SourceSet by project.sourceSets
        add(testUI)
    }
}

intellijPlatformTesting.runIde.configureEach {
    plugins {
        // Run-IDE sandbox only: Kubernetes plugin consistently fails on startup.
        disablePlugin("com.intellij.kubernetes")
        // Run-IDE sandbox only: Sass plugin logs missing color scheme resources.
        disablePlugin("org.jetbrains.plugins.sass")
    }
}

// --- Kotlin Configuration ---
kotlin {
    jvmToolchain(javaVersionStr.toInt())
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.valueOf("JVM_$javaVersionStr")
        freeCompilerArgs.add("-jvm-default=enable")
        apiVersion = KotlinVersion.KOTLIN_2_2
    }
}

// --- Mockito Agent Configuration (Root project only) ---
val mockitoAgent: Configuration = configurations.create("mockitoAgent")

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
        // UI Test framework dependencies
        testFramework(TestFrameworkType.Starter, configurationName = "testUIImplementation")
        testFramework(TestFrameworkType.JUnit5, configurationName = "testUIImplementation")
    }

    implementation(project(":jps-shared"))
    runtimeOnly(project(":jps-builder"))
    implementation(files("lib/OtpErlang.jar"))
    implementation(libCommonsIo)

    testImplementation(libMockitoCore)
    mockitoAgent(libMockitoCore) { isTransitive = false }

    // UI Test dependencies
    testUIImplementation(libs.kodein.di.jvm)
    testUIImplementation(libs.kotlinx.coroutines.core.jvm)

    // JUnit 5 is required for UI tests
    testUIImplementation(libs.junit.jupiter)
    testUIRuntimeOnly("org.junit.platform:junit-platform-launcher")

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

    val compatiblePluginsList = providers.gradleProperty("runIdeCompatiblePlugins")
        .getOrElse("")
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    // runIde uses "required plugins" mode; for IU builds that can drop com.intellij.modules.ultimate,
    // which cascades into bundled plugin dependency warnings. Force Ultimate to stay required,
    // and include any compatible plugins so they can load under required-plugins mode.
    val requiredPluginsId = listOf(
        providers.gradleProperty("pluginGroup").get(),
        "com.intellij.modules.ultimate"
    ).plus(compatiblePluginsList)
        .distinct()
        .joinToString(",")
    val platformTypeValue = providers.gradleProperty("platformType").get()
    val isRunIdeTask = name == "runIde"
    val isUltimateTask = name.contains("IntellijIdeaUltimate")
    if ((isRunIdeTask && platformTypeValue == "IU") || isUltimateTask) {
        // Ensure required-plugins mode keeps Ultimate enabled for IU runs.
        jvmArgumentProviders += CommandLineArgumentProvider {
            listOf("-Didea.required.plugins.id=$requiredPluginsId")
        }
    }

    if (project.hasProperty("runIdeWorkingDirectory") && project.property("runIdeWorkingDirectory").toString().isNotEmpty()) {
        workingDir = file(project.property("runIdeWorkingDirectory").toString())
    }

    // Development SDK paths - allows devs to autoconfigure SDKs when running the plugin
    // Usage: ./gradlew runIde -PrunIdeSdkErlangPath='/path/to/erlang' -PrunIdeSdkElixirPath='/path/to/elixir'
    if (project.hasProperty("runIdeSdkErlangPath")) {
        systemProperty("runIdeSdkErlangPath", project.property("runIdeSdkErlangPath").toString())
    }
    if (project.hasProperty("runIdeSdkElixirPath")) {
        systemProperty("runIdeSdkElixirPath", project.property("runIdeSdkElixirPath").toString())
    }

    // Dynamic plugin loading
    // Usage: -PrunIdeCompatiblePlugins="PsiViewer,com.google.ide-perf,org.jetbrains.action-tracker"
    if (compatiblePluginsList.isNotEmpty()) {
        dependencies {
            intellijPlatform { compatiblePlugins(compatiblePluginsList) }
        }
    }
}

// In gradle.properties, define platform versions like:
//
// platformVersionIntellijIdeaEAP=261-EAP-SNAPSHOT
// platformVersionIntellijIdea=2025.3.2
//
// excluding the base "platformVersion" and EAP variants (handled separately below).
//
// You can also then run with either gradlew (CLI) or using a Run Configuration in IntelliJ IDEA via:
// `gradlew runRubyMine -PplatformVersionRubyMine=2025.2` to override a specific platform version at runtime,
// which is useful for testing against multiple IDE versions without changing the build script.
val platformVersionPrefix = "platformVersion"
val runIdePlatformsList: List<String> = project.properties.keys
    .filter { it.startsWith(platformVersionPrefix) && it.length > platformVersionPrefix.length }
    .map { it.removePrefix(platformVersionPrefix) }
    .filter { !it.endsWith("EAP") }
    .sorted()

// Reduces having to download the IDEs when testing.
val enableEAP = providers.gradleProperty("enableEAPIDEs").get().toBoolean()

runIdePlatformsList.forEach { platform ->
    intellijPlatformTesting.runIde.register("run${platform}", Action {
        type = IntelliJPlatformType.valueOf(platform)
        version = providers.gradleProperty("platformVersion${platform}").get()
        prepareSandboxTask {
            sandboxDirectory = layout.buildDirectory.dir("${platform.lowercase()}-sandbox")
        }
    })

    if (enableEAP && project.hasProperty("platformVersion${platform}EAP")) {
        // If EAP is enabled in gradle.properties, and useDynamicEapVersion is enabled,
        // use the major version with "-EAP-SNAPSHOT" suffix.
        // Otherwise, use the specified EAP version.
        val platformVersionEAPValue = providers.gradleProperty("platformVersion${platform}EAP").get()
        val idePlatformTypeCode = IntelliJPlatformType.valueOf(platform)
        val ideEapVersion: String = if (useDynamicEapVersion) {
            val foundIdeVersion = VersionFetcher.getLatestEapBuild(platformType = idePlatformTypeCode.code)
            // Get the Major version, by splitting at the first dot, then appending "-EAP-SNAPSHOT"
            val majorVersion = foundIdeVersion.split(".").firstOrNull() ?: foundIdeVersion
            "$majorVersion-EAP-SNAPSHOT"
        } else {
            platformVersionEAPValue
        }
        intellijPlatformTesting.runIde.register("run${platform}EAP", Action {
            type = idePlatformTypeCode
            version = ideEapVersion
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
    src("https://github.com/joshuataylor/intellij_elixir/archive/${quoterVersion}.zip")
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

val getQuoterDeps by tasks.registering(GetQuoterDepsTask::class) {
    dependsOn(unzipQuoter, buildElixir)
    workingDir(quoterUnzippedPath)

    // Configure the task
    quoterDir.set(quoterUnzippedPath)
    depsDir.set(quoterUnzippedPath.dir("deps"))

    // INPUTS: mix.exs and mix.lock define requirements
    inputs.file(quoterUnzippedPath.file("mix.exs"))
        .withPropertyName("mixExs")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(quoterUnzippedPath.file("mix.lock"))
        .withPropertyName("mixLock")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

val releaseQuoter by tasks.registering(ReleaseQuoterTask::class) {
    dependsOn(getQuoterDeps)
    workingDir(quoterUnzippedPath)

    // Configure the task
    quoterDir.set(quoterUnzippedPath)
    buildDir.set(quoterUnzippedPath.dir("_build"))

    // INPUTS: mix.exs, lockfile, source code, and dependencies
    inputs.files(
        quoterUnzippedPath.file("mix.exs"),
        quoterUnzippedPath.file("mix.lock")
    ).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(quoterUnzippedPath.dir("lib")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(quoterUnzippedPath.dir("config")).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(quoterUnzippedPath.dir("deps")).withPathSensitivity(PathSensitivity.RELATIVE)
}

// Register ElixirService - manages Elixir installation and build
val elixirService = gradle.sharedServices.registerIfAbsent("elixir", ElixirService::class) {
    val versionString = elixirVersion
    parameters {
        elixirVersion.set(versionString)
        projectDir.set(layout.projectDirectory)
    }
}

// Register the QuoterService - Gradle calls close() at build end regardless of failure
// Depends on ElixirService for Mix commands
// See: https://docs.gradle.org/current/userguide/build_services.html
val quoterService = gradle.sharedServices.registerIfAbsent("quoter", QuoterService::class) {
    val elixirSvc = elixirService
    parameters {
        executable.set(quoterExe)
        tmpDir.set(quoterTmpPath)
        elixirService.set(elixirSvc)
    }
}

val startQuoter by tasks.registering(StartQuoterTask::class) {
    dependsOn(releaseQuoter)
}

registerResolveExternalDependenciesTasksForAllProjects()

// --- Test Configuration ---

// ALL test tasks in ALL projects use the QuoterService (ensures cleanup on any failure)
allprojects {
    tasks.withType<Test>().configureEach {

        // Validate Erlang is available before running tests
        doFirst {
            // Skip check if ERLANG_SDK_HOME is explicitly set
            val erlangSdkHome = System.getenv("ERLANG_SDK_HOME")
            if (erlangSdkHome == null || erlangSdkHome.isEmpty()) {
                val erlCommand = if (System.getProperty("os.name").lowercase().contains("windows")) "erl.exe" else "erl"
                try {
                    val process = ProcessBuilder(erlCommand, "-version")
                        .redirectErrorStream(true)
                        .start()
                    val exitCode = process.waitFor()
                    if (exitCode != 0) {
                        throw GradleException(
                            """
                            |Erlang/OTP not found or failed to run.
                            |Tests require Erlang to be installed and on PATH.
                            |
                            |Options:
                            |  1. Download from: https://www.erlang.org/downloads
                            |  2. Install via Chocolatey: choco install erlang
                            |  3. Set ERLANG_SDK_HOME environment variable to your Erlang installation directory
                            |
                            |The jps-builder tests auto-detect Erlang by running '$erlCommand -eval' command.
                            """.trimMargin()
                        )
                    }
                    logger.lifecycle("Erlang found: $erlCommand is available on PATH")
                } catch (e: java.io.IOException) {
                    throw GradleException(
                        """
                        |Erlang/OTP executable '$erlCommand' not found on PATH.
                        |Tests require Erlang to be installed and on PATH.
                        |
                        |Options:
                        |  1. Download from: https://www.erlang.org/downloads
                        |  2. Install via Chocolatey: choco install erlang
                        |  3. Set ERLANG_SDK_HOME environment variable to your Erlang installation directory
                        |
                        |The jps-builder tests auto-detect Erlang by running '$erlCommand -eval' command.
                        """.trimMargin(),
                        e
                    )
                }
            } else {
                logger.lifecycle("Using ERLANG_SDK_HOME from environment: $erlangSdkHome")
            }
        }
    }
}

tasks.named<Test>("test") {
    dependsOn("prepareTestSandbox", startQuoter)
    usesService(quoterService)

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


/**
 * Helper function to get SDK path from mise.
 * Requires mise to be installed and available in PATH.
 *
 * @param tool The tool name (e.g., "erlang", "elixir")
 * @return The absolute path to the tool installation
 * @throws GradleException if mise is not installed or tool not found
 */
fun getMiseSdkPath(tool: String): Provider<String> {
    return providers.exec {
        commandLine("mise", "where", tool)
    }.standardOutput.asText.map { it.trim() }.orElse(
        providers.provider {
            throw GradleException(
                """
                |Failed to get path for '$tool' from mise.
                |
                |UI tests require mise to be installed and configured with .tool-versions.
                |
                |Installation:
                |  Linux/macOS: curl https://mise.run | sh
                |  Windows: https://mise.jdx.dev/getting-started.html#windows
                |
                |Setup:
                |  1. Install mise
                |  2. Run: mise install inside the project directory
                |  3. Verify: mise where $tool
                |
                |See: https://mise.jdx.dev/
                """.trimMargin()
            )
        }
    )
}

tasks.register<Test>("testUI") {
    dependsOn(tasks.buildPlugin, tasks.prepareSandbox, unzipQuoter)
    description = "Runs only the UI tests that start the IDE"
    group = "verification"

    testClassesDirs = sourceSets["testUI"].output.classesDirs
    classpath = sourceSets["testUI"].runtimeClasspath

    useJUnitPlatform()

    // UI tests should run sequentially (not in parallel) to avoid conflicts
    maxParallelForks = 1

    // Increase memory for UI tests
    minHeapSize = "1g"
    maxHeapSize = "4g"

    systemProperty("path.to.build.plugin", tasks.buildPlugin.get().archiveFile.get().asFile.absolutePath)
    systemProperty("idea.home.path", tasks.prepareTestSandbox.get().getDestinationDir().parentFile.absolutePath)
    systemProperty("uiPlatformBuildVersion", actualPlatformVersion)
    systemProperty("projectPath", unzipQuoter.get().destinationDir.absolutePath)

    // Disable IntelliJ test listener that conflicts with standard JUnit
    systemProperty("idea.test.cyclic.buffer.size", "0")

    // Get SDK paths from mise (reads .tool-versions) at execution time
    doFirst {
        systemProperty("erlangSdkPath", getMiseSdkPath("erlang").get())
        systemProperty("elixirSdkPath", getMiseSdkPath("elixir").get())
    }

    // Add required JVM arguments
    jvmArgumentProviders += CommandLineArgumentProvider {
        mutableListOf(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.desktop/javax.swing=ALL-UNNAMED"
        )
    }

}

// Uncomment to allow using build-scan.
//if (hasProperty("buildScan")) {
//    extensions.findByName("buildScan")?.withGroovyBuilder {
//        setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
//        setProperty("termsOfServiceAgree", "yes")
//    }
//}
