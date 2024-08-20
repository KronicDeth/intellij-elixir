import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("org.jetbrains.intellij.platform") version "2.0.1"
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("de.undercouch.download") version "4.1.2"
    id("com.adarshr.test-logger") version "4.0.0"
}

// Define paths and versions using properties from gradle.properties
val cachePath by extra("${rootDir}/cache")
val elixirVersion: String by project
val elixirPath by extra("${cachePath}/elixir-$elixirVersion")
val quoterVersion by extra("2.1.0")
val quoterUnzippedPath by extra("${cachePath}/elixir-$elixirVersion-intellij_elixir-$quoterVersion")
val quoterReleasePath by extra("$quoterUnzippedPath/_build/dev/rel/intellij_elixir")
val quoterExe by extra("$quoterReleasePath/bin/intellij_elixir")
val quoterZipPath by extra("$cachePath/intellij_elixir-$quoterVersion.zip")
val quoterZipRootPath by extra("$cachePath/intellij_elixir-$quoterVersion")

// Determine if this is a release build
val isRelease = project.hasProperty("isRelease") && project.property("isRelease") as Boolean
val versionSuffix = if (isRelease) "" else "-pre+${SimpleDateFormat("yyyyMMddHHmmss").format(Date())}"
val channel = if (isRelease) "default" else "canary"

// Function to get environment variables
fun environment(key: String) = providers.environmentVariable(key)

// Set the version of the plugin
version = "${project.property("pluginVersion")}$versionSuffix"

allprojects {
    apply(plugin = "java")
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

subprojects {
    apply(plugin = "org.jetbrains.intellij.platform.module")
    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
        }
    }
    dependencies {
        testImplementation("junit:junit:4.13.2")
        testImplementation("org.opentest4j:opentest4j:1.3.0")

        intellijPlatform {
            create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

            bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',').toList() })
            plugins(providers.gradleProperty("platformPlugins").map { it.split(',').toList() })

            instrumentationTools()
            pluginVerifier()
            zipSigner()
            testFramework(TestFrameworkType.Platform)
            testFramework(TestFrameworkType.Plugin.Java)
        }
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

sourceSets {
    main {
        java.srcDirs("src", "gen")
        resources.srcDirs("resources")
    }
    test {
        java.srcDir("tests")
    }
}

intellijPlatform {
    pluginConfiguration {
        val stripTag = { text: String, tag: String -> text.replace("<$tag>", "").replace("</$tag>", "") }
        val bodyInnerHTML = { path: String ->
            stripTag(stripTag(file(path).readText(), "html"), "body")
        }

        id = providers.gradleProperty("pluginGroup")
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")
        changeNotes.set(bodyInnerHTML("resources/META-INF/changelog.html"))
        description.set(bodyInnerHTML("resources/META-INF/description.html"))

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
    publishing {
        token = environment("PUBLISH_TOKEN")
        channels = providers.gradleProperty("pluginVersion").map {
            listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }
    pluginVerification {
        ides {
            ide(IntelliJPlatformType.IntellijIdeaCommunity, "2024.2.0.1")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        apiVersion = "1.7"
        jvmTarget = "21"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

val compilationPackages = listOf("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")

tasks.test {
    environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
    environment("ELIXIR_EBIN_DIRECTORY", "$elixirPath/lib/elixir/ebin/")
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

intellijPlatformTesting {
    val platformsList = providers.gradleProperty("platformsList").get().split(",")

    val runIdePluginsProperty = providers.gradleProperty("runIdePlugins").getOrElse("")
    val runIdePluginsList = runIdePluginsProperty.split(",")

    platformsList.forEach { platform ->
        runIde.create("run$platform") {
            type = IntelliJPlatformType.valueOf(platform)
            version = providers.gradleProperty("platformVersion$platform").get()
            prepareSandboxTask {
                sandboxDirectory = project.layout.buildDirectory.dir("${platform.lowercase()}-sandbox")
            }

            if (runIdePluginsList.isNotEmpty()) {
                plugins {
                    runIdePluginsList.forEach { plugin ->
                        plugins(plugin.trim())
                    }
                }
            }
        }
    }
}

tasks.register<Test>("testCompilation") {
    group = "Verification"
    dependsOn("classes", "testClasses")
    useJUnit {
        include(compilationPackages)
    }
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

repositories {
    maven { url = uri("https://maven-central.storage.googleapis.com") }
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',').toList() })
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',').toList() })

        instrumentationTools()
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    implementation(project(":jps-builder"))
    implementation(project(":jps-shared"))
    implementation(files("lib/OtpErlang.jar"))
    implementation("commons-io:commons-io:2.16.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.opentest4j:opentest4j:1.3.0")

    testImplementation("org.mockito:mockito-core:2.2.9")
    testImplementation("org.objenesis:objenesis:2.4")
}

tasks.compileJava {
    dependsOn(":jps-shared:composedJar")
    dependsOn(":jps-builder:composedJar")
}

apply(plugin = "idea")

idea {
    project {
        jdkName = providers.gradleProperty("javaVersion").get()
        languageLevel = providers.gradleProperty("javaVersion").get().let { org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(it) }
    }
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

tasks.register("getElixir") {
    doLast {
        val folder = File(elixirPath)

        if (!folder.isDirectory || folder.list().isEmpty()) {
            tasks.named<de.undercouch.gradle.tasks.download.Download>("download") {
                src("https://github.com/elixir-lang/elixir/archive/v$elixirVersion.zip")
                dest("$rootDir/cache/Elixir.$elixirVersion.zip")
                overwrite(false)
            }
        }

        val binFolder = File("$elixirPath/bin")
        if (!binFolder.isDirectory || folder.list().isEmpty()) {
            copy {
                from(zipTree("$rootDir/cache/Elixir.$elixirVersion.zip"))
                into("$rootDir/cache/")
            }

            exec {
                workingDir = file(elixirPath)
                commandLine("make")
            }
        }
    }
}

tasks.register("getQuoter") {
    doLast {
        tasks.named<de.undercouch.gradle.tasks.download.Download>("download") {
            src("https://github.com/KronicDeth/intellij_elixir/archive/v$quoterVersion.zip")
            dest(quoterZipPath)
            overwrite(false)
        }

        val folder = File(quoterUnzippedPath)
        if (!folder.isDirectory || folder.list().isEmpty()) {
            copy {
                from(zipTree(quoterZipPath))
                into(cachePath)
            }

            val quoterZipRootFile = File(quoterZipRootPath)

            quoterZipRootFile.renameTo(folder)
        }
    }
}

tasks.register("getQuoterDeps") {
    dependsOn("getQuoter")
    doLast {
        exec {
            workingDir = file(quoterUnzippedPath)
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
                workingDir = file(quoterUnzippedPath)
                commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get,", "release")
            }
        }
    }
}

tasks.compileTestJava {
    dependsOn(":jps-builder:composedJar")
    dependsOn(":jps-shared:composedJar")
    dependsOn("getElixir")
    dependsOn("getQuoter")
}

tasks.register<Exec>("runQuoter") {
    dependsOn("releaseQuoter")
    environment("RELEASE_COOKIE", "intellij_elixir")
    environment("RELEASE_DISTRIBUTION", "name")
    environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
    executable = quoterExe
    args("daemon")
}

tasks.register<Exec>("stopQuoter") {
    dependsOn("releaseQuoter")
    environment("RELEASE_COOKIE", "intellij_elixir")
    environment("RELEASE_DISTRIBUTION", "name")
    environment("RELEASE_NAME", "intellij_elixir@127.0.0.1")
    executable = quoterExe
    args("stop")
}

tasks.named<JavaExec>("runIde") {
    systemProperty("idea.log.debug.categories", "org.elixir_lang=TRACE")
    jvmArgs(
        "-Didea.debug.mode=true",
        "-XX:+AllowEnhancedClassRedefinition",
        "-Didea.is.internal=true",
        "-Dlog4j2.debug=true",
        "-Dlogger.org=TRACE",
        "-Didea.ProcessCanceledException=disabled"
    )
    maxHeapSize = "7g"
    if (project.hasProperty("runIdeWorkingDirectory") && project.property("runIdeWorkingDirectory").toString().isNotEmpty()) {
        workingDir = project.property("runIdeWorkingDirectory")?.let { file(it) }!!
    }
}

tasks.test {
    dependsOn("runQuoter")
    finalizedBy("stopQuoter")
}

apply(plugin = "idea")

idea {
    project {
        jdkName = providers.gradleProperty("javaVersion").get()
        languageLevel = providers.gradleProperty("javaVersion").get()
            .let { org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(it) }
    }
    module {
        generatedSourceDirs.add(file("gen"))
    }
}