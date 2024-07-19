import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
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
    id("com.adarshr.test-logger") version "4.0.0"
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
    buildSearchableOptions = false
    instrumentCode = false

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

    compileJava {
        sourceCompatibility = properties("javaVersion").get()
        targetCompatibility = properties("javaVersion").get()
    }

//    withType<JavaCompile> {
//        sourceCompatibility =
//        targetCompatibility = "17"
//    }
//
    runIde {
        systemProperty("idea.log.debug.categories", "org.intellij_lang=TRACE")
        jvmArgs("-Didea.debug.mode=true", "-Didea.is.internal=true", "-Dlog4j2.debug=true", "-Dlogger.org=TRACE")
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
        environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
        environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
        environment("ELIXIR_VERSION", elixirVersion)
        isScanForTestClasses = false
        include("**/Issue*.class")
        include("**/*Test.class")
        include("**/*TestCase.class")

        useJUnit {
            exclude(compilationPackages)
        }
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    register<Test>("testCompilation") {
        group = "Verification"
        dependsOn("classes", "testClasses")
        useJUnit {
            exclude(compilationPackages)
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
                standardOutput = System.out
                errorOutput = System.err
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
                standardOutput = System.out
                errorOutput = System.err

            }
        }
    }

    // @todo should this also delete the files before release?
    register("releaseQuoter") {
        dependsOn("getQuoterDeps")
        doLast {
            exec {
                workingDir = file(quoterUnzippedPath)
                commandLine(
                    "mix",
                    "do",
                    "local.rebar",
                    "--force,",
                    "local.hex",
                    "--force,",
                    "deps.get,",
                    "release",
                    "--overwrite"
                )
                standardOutput = System.out
                errorOutput = System.err

            }
        }
    }

    compileTestJava {
        dependsOn("getElixir", "getQuoter")
    }

    register("runQuoter") {
        dependsOn("releaseQuoter")
        doLast {
            exec {
                environment("RELEASE_COOKIE", "intellij_elixir")
                environment("RELEASE_DISTRIBUTION", "name")
                environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
                commandLine(quoterExe, "daemon")
                standardOutput = System.out
                errorOutput = System.err
            }
        }

    }

    register("stopQuoter") {
        dependsOn("releaseQuoter")
        doLast {
            exec {
                environment("RELEASE_COOKIE", "intellij_elixir")
                environment("RELEASE_DISTRIBUTION", "name")
                environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
                commandLine(quoterExe, "stop")
                standardOutput = System.out
                errorOutput = System.err
                isIgnoreExitValue = true  // Sometimes the exit
            }
        }
    }

    test {
        dependsOn("runQuoter")
        finalizedBy("stopQuoter")
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

    tasks.test {
        environment("ELIXIR_LANG_ELIXIR_PATH", rootProject.extra["elixirPath"] as String)
        environment("ELIXIR_EBIN_DIRECTORY", "${rootProject.extra["elixirPath"]}/lib/elixir/ebin/")
        environment("ELIXIR_VERSION", rootProject.extra["elixirVersion"] as String)

        useJUnit()
        exclude(compilationPackages)

        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}
//
project(":jps-builder") {
    apply(plugin = "java")

    dependencies {
        implementation(project(":jps-shared"))
    }
}