import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import ReleaseQuoterTask
import org.jetbrains.intellij.platform.gradle.GradleProperties

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
    alias(libs.plugins.gradleDownloadTask)
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

val cachePath = "${rootDir}/cache"
val elixirVersion = properties("elixirVersion").get()
val elixirPath = "${cachePath}/elixir-${elixirVersion}"
val quoterVersion = "2.1.0"

val quoterUnzippedPathValue = "${cachePath}/elixir-${elixirVersion}-intellij_elixir-${quoterVersion}"
val quoterReleasePathValue = "${quoterUnzippedPathValue}/intellij_elixir-${quoterVersion}/_build/dev/rel/intellij_elixir"
val tmpDirPath = "${rootDir}/cachetmp"
val quoterExe = "${quoterReleasePathValue}/bin/intellij_elixir"
val quoterZipPathValue = "${cachePath}/intellij_elixir-${quoterVersion}.zip"

val versionSuffix = if (project.hasProperty("isRelease") && project.property("isRelease") == "true") {
    ""
} else {
    val buildTime: String = ZonedDateTime
        .now(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

    "-pre+${buildTime}"
}
val channel = if (project.hasProperty("isRelease") && project.property("isRelease") == "true") "default" else "canary"

version = "${properties("pluginVersion").get()}$versionSuffix"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    implementation(libs.annotations)
    implementation(project(":jps-builder"))
    implementation(project(":jps-shared"))
    implementation(files("lib/OtpErlang.jar"))
    implementation("commons-io:commons-io:2.16.1")
    testImplementation("org.mockito:mockito-core:2.2.9")
    testImplementation("org.objenesis:objenesis:2.4")

    intellijPlatform {
        intellijIdeaCommunity(properties("platformVersion"))
        instrumentationTools()
        bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })
        plugins(properties("platformPlugins").map { it.split(',') })
        testFramework(TestFrameworkType.Platform)
    }
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("tests")
    }
}

idea {
    project {
        jdkName = properties("javaVersion").get()
//        languageLevel = IdeaLanguageLevel.JDK_17
    }
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

intellijPlatform {
//    buildSearchableOptions = false
//    instrumentCode = false

    pluginConfiguration {
        id = properties("pluginGroup")
        name = properties("pluginName")
        version = properties("pluginVersion")
        description = properties("pluginDescription")
        changeNotes = properties("pluginChangeNotes")
        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
        vendor {
            name = properties("vendorName")
            email = properties("vendorEmail")
            url = properties("pluginRepositoryUrl")
        }
    }

    signing {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = environment("PUBLISH_TOKEN")
        channels = properties("pluginVersion").map {
            listOf(
                it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

val compilationPackages = listOf("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = properties("javaVersion").get()
    }

    register<Delete>("deleteCache") {
        delete("cache")
        delete("build")
        delete("jps-shared/build")
        delete("jps-builder/build")
    }

    clean {
        dependsOn("deleteCache")
    }

    compileJava {
        dependsOn(":jps-builder:composedJar", ":jps-shared:composedJar")
        sourceCompatibility = properties("javaVersion").get()
        targetCompatibility = properties("javaVersion").get()
    }
    named("compileTestJava") {
        dependsOn(":jps-builder:composedJar", ":jps-shared:composedJar", "makeElixir")
    }

    runIde {
        systemProperty("idea.log.info.categories", "org.intellij_lang=TRACE")
        jvmArgs("-Didea.info.mode=true", "-Didea.is.internal=true", "-Dlog4j2.info=true", "-Dlogger.org=TRACE")
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    testIdeUi {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
    }

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

    // quoter
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
        dependsOn("downloadQuoter", "extractElixir")  // Add "extractElixir" here
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

    // Task to run the Quoter daemon
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

    // Task to stop the Quoter daemon
    register<Exec>("stopQuoter") {
        dependsOn("releaseQuoter")
//        logging.captureStandardOutput(LogLevel.LIFECYCLE)
//        logging.captureStandardError(LogLevel.ERROR)

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

allprojects {
    extra["elixirPath"] = elixirPath
    apply(plugin = "java")
    apply(plugin = "kotlin")

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
subprojects {
    apply(plugin = "org.jetbrains.intellij.platform.module")
    apply(plugin = "java")

    extra["elixirPath"] = elixirPath
    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
            jetbrainsRuntime()
        }
    }
    dependencies {
        intellijPlatform {
            intellijIdeaCommunity(properties("platformVersion"))
            bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })
            plugins(properties("platformPlugins").map { it.split(',') })
            testFramework(TestFrameworkType.Platform)
            instrumentationTools()
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
//
project(":jps-builder") {
    apply(plugin = "java")

    dependencies {
        implementation(project(":jps-shared"))
    }
}