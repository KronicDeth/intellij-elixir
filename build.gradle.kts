import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
    id("de.undercouch.download") version "4.1.2"
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

val cachePath = "${rootDir}/cache"
val elixirVersion = properties("elixirVersion").get()
val elixirPath = "${cachePath}/elixir-${elixirVersion}"
val quoterVersion = "2.1.0"
val quoterUnzippedPath = "${cachePath}/elixir-${elixirVersion}-intellij_elixir-${quoterVersion}"
val quoterReleasePath = "${quoterUnzippedPath}/_build/dev/rel/intellij_elixir"
val quoterExe = "${quoterReleasePath}/bin/intellij_elixir"
val quoterZipPath = "${cachePath}/intellij_elixir-${quoterVersion}.zip"
val quoterZipRootPath = "${cachePath}/intellij_elixir-${quoterVersion}"

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
        bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })
        plugins(properties("platformPlugins").map { it.split(',') })
        testFramework(TestFrameworkType.Bundled)
    }
}

kotlin {
    jvmToolchain(17)
}
intellijPlatform {
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
        channels = properties("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }
}

changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

tasks {
    runIde {
        systemProperty("idea.log.debug.categories", "org.intellij_lang=TRACE")
        jvmArgs("-Didea.debug.mode=true", "-Didea.is.internal=true", "-Dlog4j2.debug=true", "-Dlogger.org=TRACE")
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    buildSearchableOptions {
        enabled = false
    }

    test {
        environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
        environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
        environment("ELIXIR_VERSION", elixirVersion)
        useJUnit {
            exclude("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")
        }
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    register<de.undercouch.gradle.tasks.download.Download>("getElixir") {
        src("https://github.com/elixir-lang/elixir/archive/v${elixirVersion}.zip")
        dest("${rootDir}/cache/Elixir.${elixirVersion}.zip")
        overwrite(false)
        doLast {
            copy {
                from(zipTree("${rootDir}/cache/Elixir.${elixirVersion}.zip"))
                into("${rootDir}/cache/")
            }
            exec {
                workingDir = file(elixirPath)
                commandLine("make")
            }
        }
    }

    register<de.undercouch.gradle.tasks.download.Download>("getQuoter") {
        src("https://github.com/KronicDeth/intellij_elixir/archive/v${quoterVersion}.zip")
        dest(quoterZipPath)
        overwrite(false)
        doLast {
            copy {
                from(zipTree(quoterZipPath))
                into(cachePath)
            }
            file(quoterZipRootPath).renameTo(file(quoterUnzippedPath))
        }
    }

    register("getQuoterDeps") {
        dependsOn("getQuoter")
        doLast {
            exec {
                workingDir = file(quoterUnzippedPath)
                commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get")
            }
        }
    }

    register("releaseQuoter") {
        dependsOn("getQuoterDeps")
        doLast {
            exec {
                workingDir = file(quoterUnzippedPath)
                commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get,", "release")
            }
        }
    }

    compileTestJava {
        dependsOn("getElixir", "getQuoter")
    }

    register<Exec>("runQuoter") {
        dependsOn("releaseQuoter")
        environment("RELEASE_COOKIE", "intellij_elixir")
        environment("RELEASE_DISTRIBUTION", "name")
        environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
        executable(quoterExe)
        args("daemon")
    }

    register<Exec>("stopQuoter") {
        dependsOn("releaseQuoter")
        environment("RELEASE_COOKIE", "intellij_elixir")
        environment("RELEASE_DISTRIBUTION", "name")
        environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
        executable(quoterExe)
        args("stop")
    }

    test {
        dependsOn("runQuoter")
        finalizedBy("stopQuoter")
    }
}

project(":jps-shared") {
    apply(plugin = "kotlin")
    apply(plugin = "java")

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains:jps-build-api:241.18034.62")
        implementation("org.jetbrains.intellij.deps:jdom:2.0.6")
        implementation("com.intellij:annotations:12.0")
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

project(":jps-builder") {
    apply(plugin = "kotlin")
    apply(plugin = "java")

    dependencies {
        implementation(project(":jps-shared"))
    }
}

allprojects {
    apply(plugin = "org.jetbrains.intellij.platform")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://www.jetbrains.com/intellij-repository/snapshots/")
        intellijPlatform {
            defaultRepositories()
        }
    }
    extra["elixirPath"] = elixirPath
}