//import org.jetbrains.intellij.platform.gradle.extensions.TestFrameworkType

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // Kotlin support
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
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

val pluginVersion: String by project
val versionSuffix = if (project.hasProperty("isRelease") && project.property("isRelease") == "true") {
    ""
} else {
    "xxx"
//    "-pre+${java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(java.time.ZoneOffset.UTC).format(java.time.Instant.now())}"
}
val channel = if (project.hasProperty("isRelease") && project.property("isRelease") == "true") "default" else "canary"

version = "$pluginVersion$versionSuffix"

allprojects {
    // apply(plugin = "java")
    // apply(plugin = "org.jetbrains.intellij")

    // java {
    //     sourceCompatibility = JavaVersion.VERSION_17
    //     targetCompatibility = JavaVersion.VERSION_17
    // }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(17)
}


// Configure project's dependencies
repositories {
    mavenCentral()
    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        maven("https://www.jetbrains.com/intellij-repository/snapshots/")
        defaultRepositories()
    }
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    implementation(libs.annotations)
    implementation(project(":jps-builder"))
    implementation(project(":jps-shared"))
    implementation(files("lib/OtpErlang.jar"))
    implementation("commons-io:commons-io:2.16.1")
    testImplementation("org.mockito:mockito-core:2.2.9")
    testImplementation("org.objenesis:objenesis:2.4")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(properties("platformType"), properties("platformVersion"))

        // pycharmProfessional(properties("platformVersion"))
        bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(properties("platformPlugins").map { it.split(',') })

        // instrumentationTools()
        // pluginVerifier()
//        testFramework(TestFrameworkType.Platform)
    }
}

//project(":jps-builder") {
//    dependencies {
//        implementation(project(":jps-shared"))
//    }
//}

project(":jps-shared") {
    apply(plugin = "kotlin")
    apply(plugin = "java")

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains:jps-build-api:241.8102.112")
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

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    // sandboxContainer = file("/Users/josh/.ide/config/pycharmdev")
    instrumentCode = false

    pluginConfiguration {
        version = properties("pluginVersion")

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild")
            untilBuild = properties("pluginUntilBuild")
        }
    }

    signing {
        certificateChain = environment("CERTIFICATE_CHAIN")
        privateKey = environment("PRIVATE_KEY")
        password = environment("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = environment("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = properties("pluginVersion").map {
            listOf(
                it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }

//    verifyPlugin {
//        ides {
//            recommended()
//        }
//    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
//kv {
//    defaults {
//        xml {
//            onCheck = true
//        }
//    }
//}

tasks {
    runIde {
        systemProperty("idea.log.debug.categories", "org.intellij_lang=TRACE")
        jvmArgs("-Didea.debug.mode=true", "-Didea.is.internal=true", "-Dlog4j2.debug=true", "-Dlogger.org=TRACE")
        // jvmArgs("-Didea.ProcessCanceledException=disabled")
        // maxHeapSize = "7g"
    }
    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }

    // Configure UI tests plugin
    // Read more: https://github.com/JetBrains/intellij-ui-test-robot
    testIdeUi {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
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

    // register<Test>("testCompilation") {
    //     group = "Verification"
    //     useJUnit {
    //         include("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")
    //     }
    //     testLogging {
    //         exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    //     }
    // }
}



tasks.register("getElixir") {
    doLast {
        val folder = File(elixirPath)
        if (!folder.isDirectory || folder.list()?.isEmpty() == true) {
            de.undercouch.gradle.tasks.download.Download::class.java.getDeclaredConstructor().newInstance().apply {
                src("https://github.com/elixir-lang/elixir/archive/v${elixirVersion}.zip")
                dest("${rootDir}/cache/Elixir.${elixirVersion}.zip")
                overwrite(false)
            }.download()
        }

        val binFolder = File("${elixirPath}/bin")
        if (!binFolder.isDirectory || folder.list()?.isEmpty() == true) {
            copy {
                from(zipTree("${rootDir}/cache/Elixir.${elixirVersion}.zip"))
                into("${rootDir}/cache/")
            }

            exec {
                workingDir = File(elixirPath)
                commandLine("make")
            }
        }
    }
}

tasks.register("getQuoter") {
    doLast {
        de.undercouch.gradle.tasks.download.Download::class.java.getDeclaredConstructor().newInstance().apply {
            src("https://github.com/KronicDeth/intellij_elixir/archive/v${quoterVersion}.zip")
            dest(quoterZipPath)
            overwrite(false)
        }.download()

        val folder = File(quoterUnzippedPath)
        if (!folder.isDirectory || folder.list()?.isEmpty() == true) {
            copy {
                from(zipTree(quoterZipPath))
                into(cachePath)
            }

            File(quoterZipRootPath).renameTo(File(quoterUnzippedPath))
        }
    }
}

tasks.register("getQuoterDeps") {
    dependsOn("getQuoter")
    doLast {
        exec {
            workingDir = File(quoterUnzippedPath)
            commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get")
        }
    }
}

tasks.register("releaseQuoter") {
    dependsOn("getQuoterDeps")
    doLast {
        val file = File(quoterExe)
        if (!file.canExecute()) {
            exec {
                workingDir = File(quoterUnzippedPath)
                commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get,", "release")
            }
        }
    }
}

tasks.compileTestJava {
    dependsOn("getElixir", "getQuoter")
}

tasks.register<Exec>("runQuoter") {
    dependsOn("releaseQuoter")
    environment("RELEASE_COOKIE", "intellij_elixir")
    environment("RELEASE_DISTRIBUTION", "name")
    environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
    executable(quoterExe)
    args("daemon")
}

tasks.register<Exec>("stopQuoter") {
    dependsOn("releaseQuoter")
    environment("RELEASE_COOKIE", "intellij_elixir")
    environment("RELEASE_DISTRIBUTION", "name")
    environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
    executable(quoterExe)
    args("stop")
}

tasks.test {
    dependsOn("runQuoter")
    finalizedBy("stopQuoter")
}