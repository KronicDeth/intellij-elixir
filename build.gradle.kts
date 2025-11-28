import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import javax.xml.parsers.DocumentBuilderFactory
import java.net.URI
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("org.jetbrains.intellij.platform") version "2.10.5"
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
    id("de.undercouch.download") version "5.6.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

// Function to fetch the latest EAP build number dynamically
fun latestEapBuild(): String {
    // 1) Prefer JetBrains Releases API to get a concrete numeric EAP build (e.g., 253.20558.101)
    val apiUris = listOf(
        URI("https://data.services.jetbrains.com/products/releases?code=IIU&type=eap&latest=true&fields=build"),
        URI("https://data.services.jetbrains.com/products/releases?code=IIU&type=eap&latest=true")
    )
    for (uri in apiUris) {
        val json = kotlin.runCatching {
            uri.toURL().openStream().bufferedReader().use { it.readText() }
        }.getOrNull()
        if (json != null) {
            val regex = """"build"\s*:\s*"([0-9.]+)"""".toRegex()
            val match = regex.find(json)
            if (match != null) {
                return match.groupValues[1] // e.g., 253.20558.101
            }
        }
    }

    // 2) Fallback: parse snapshots metadata and pick the last 3-part numeric EAP snapshot, then strip the suffix
    val snapshotsUri =
        URI("https://cache-redirector.jetbrains.com/www.jetbrains.com/intellij-repository/snapshots/com/jetbrains/intellij/idea/ideaIU/maven-metadata.xml")
    val versions = kotlin.runCatching {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(snapshotsUri.toURL().openStream())
        doc.documentElement.normalize()
        val versioning = doc.getElementsByTagName("versioning").item(0)
        val versionsNode = versioning?.childNodes
        buildList {
            if (versionsNode != null) {
                for (i in 0 until versionsNode.length) {
                    val n = versionsNode.item(i)
                    if (n.nodeName == "versions") {
                        val children = n.childNodes
                        for (j in 0 until children.length) {
                            val v = children.item(j)
                            if (v.nodeName == "version") add(v.textContent)
                        }
                    }
                }
            }
        }
    }.getOrNull().orEmpty()

    // Look for versions like 253.17525.95-EAP-SNAPSHOT and convert to 253.17525.95
    val numericEap = versions.asSequence()
        .mapNotNull { v ->
            val m = Regex("""^(\d+\.\d+\.\d+)-EAP-SNAPSHOT$""").matchEntire(v)
            m?.groupValues?.get(1)
        }
        .lastOrNull()

    if (numericEap != null) return numericEap

    throw IllegalStateException("No numeric EAP build found from JetBrains API or snapshots metadata")
}

// Define extra properties
val cachePath: String by extra { "${rootDir}/cache" }
val elixirVersion: String by extra { project.property("elixirVersion") as String }
val elixirPath: String by extra { "${cachePath}/elixir-${elixirVersion}" }
val quoterVersion: String by extra { project.property("quoterVersion") as String }
val pluginVersion: String by extra { project.property("pluginVersion") as String }
val useDynamicEapVersion: Boolean by extra { (project.property("useDynamicEapVersion") as String).toBoolean() }
val actualPlatformVersion: String by extra {
    if (useDynamicEapVersion) latestEapBuild() else project.property("platformVersion") as String
}

println("Building against IntelliJ Platform version: $actualPlatformVersion")

val quoterUnzippedPath: String by extra { "${cachePath}/elixir-${elixirVersion}-intellij_elixir-${quoterVersion}" }
val quoterReleasePath: String by extra { "${quoterUnzippedPath}/_build/dev/rel/intellij_elixir" }
val quoterExe: String by extra { "${quoterReleasePath}/bin/intellij_elixir" }
val quoterZipPath: String by extra { "${cachePath}/intellij_elixir-${quoterVersion}.zip" }
val quoterZipRootPath: String by extra { "${cachePath}/intellij_elixir-${quoterVersion}" }

val versionSuffix: String by extra {
    if (project.hasProperty("isRelease") && project.property("isRelease").toString().toBoolean()) {
        ""
    } else {
        val date = SimpleDateFormat("yyyyMMddHHmmss").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
        "-pre+$date"
    }
}

val channel: String by extra {
    if (project.hasProperty("isRelease") && project.property("isRelease").toString().toBoolean()) {
        "default"
    } else {
        "canary"
    }
}

val publishChannels: String by extra { project.property("publishChannels") as String }

version = "$pluginVersion$versionSuffix"

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.adarshr.test-logger")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("junit:junit:4.13.2")
        testImplementation("org.opentest4j:opentest4j:1.3.0")
    }

    // Configure test-logger plugin for consistent console output across all projects
    configure<com.adarshr.gradle.testlogger.TestLoggerExtension> {
        theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
        showExceptions = true
        showStackTraces = true
        showFullStackTraces = false
        showCauses = true
        slowThreshold = 2000
        showSummary = true
        showStandardStreams = false
        showPassedStandardStreams = false
        showSkippedStandardStreams = false
        showFailedStandardStreams = true
    }

    tasks.withType<Test> {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.intellij.platform.module")

    repositories {
        intellijPlatform {
            defaultRepositories()
        }
    }

    dependencies {
        intellijPlatform {
            create(providers.gradleProperty("platformType"), provider { actualPlatformVersion })

            bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(",").toList() })
            bundledModules(providers.gradleProperty("platformBundledModules").map { it.split(",").toList() })

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
    // buildSearchableOptions = false
    // instrumentCode = false
    pluginConfiguration {
        fun stripTag(text: String, tag: String): String =
            text.replace("<${tag}>", "").replace("</${tag}>", "")

        fun bodyInnerHTML(path: String): String =
            stripTag(stripTag(file(path).readText(), "html"), "body")

        id = providers.gradleProperty("pluginGroup")
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")
        changeNotes.set(bodyInnerHTML("resources/META-INF/changelog.html"))
        description.set(bodyInnerHTML("resources/META-INF/description.html"))

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            // We want users to be able to install the plugin on future versions, and if there is incompatibility,
            // they should hopefully create an issue :-).
            untilBuild = provider { null }
        }
        vendor {
            name = providers.gradleProperty("vendorName")
            email = providers.gradleProperty("vendorEmail")
            url = providers.gradleProperty("pluginRepositoryUrl")
        }
    }

    publishing {
        token = provider {
            System.getenv("JET_BRAINS_MARKETPLACE_TOKEN")
        }
        channels = listOf(publishChannels.split(",")).flatten()
    }

    pluginVerification {
        ides {
            // https://www.jetbrains.com/idea/download/other.html
            // Note: Using IntellijIdeaUltimate as IC is no longer available in 2025.3+
//            create(IntelliJPlatformType.IntellijIdeaCommunity, "2024.2.6")
//            create(IntelliJPlatformType.IntellijIdeaCommunity, "2024.3.6")
//            create(IntelliJPlatformType.IntellijIdeaUltimate, "2025.2.1")
            // Testing against 2025.3 EAP - using the same build we're compiling against
            create(IntelliJPlatformType.IntellijIdeaUltimate, actualPlatformVersion)
        }
    }
}

apply(plugin = "kotlin")

// Configure all RunIdeTask instances (including the new platform-specific ones)
tasks.withType<RunIdeTask>().configureEach {
    // Set JVM arguments
    jvmArguments.addAll(listOf(
        "-Didea.debug.mode=true",
        "-Didea.is.internal=true",
        "-Dlog4j2.debug=true",
        "-Dlogger.org=TRACE",
        "-XX:+AllowEnhancedClassRedefinition"
    ))

    // Set system properties to debug log
    systemProperty("idea.log.debug.categories", "org.elixir_lang")

    // Set the maximum heap size
    maxHeapSize = "7g"

    // Optionally set the working directory if specified in the project properties
    if (project.hasProperty("runIdeWorkingDirectory") && project.property("runIdeWorkingDirectory").toString().isNotEmpty()) {
        workingDir = file(project.property("runIdeWorkingDirectory").toString())
    }

    val compatiblePluginsList = providers.gradleProperty("runIdeCompatiblePlugins").get().let {
        if (it.isEmpty()) emptyList() else it.split(",")
    }
    if (compatiblePluginsList.isNotEmpty()) {
        dependencies {
            intellijPlatform {
                plugins(compatiblePluginsList)
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjvm-default=all")
        apiVersion.set(KotlinVersion.KOTLIN_2_2)
    }
}

tasks.named<Test>("test") {
    environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
    environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
    environment("ELIXIR_VERSION", elixirVersion)
    isScanForTestClasses = false
    include("**/Issue*.class")
    include("**/*Test.class")
    include("**/*TestCase.class")

    // Exclude specific abstract base test classes so that they don't show as Ignored
    exclude("**/org/elixir_lang/PlatformTestCase.class")
    exclude("**/org/elixir_lang/eex/lexer/look_ahead/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/TokenTest.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group/quote/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group/sigil/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/quote/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/sigil/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/sigil/char_list/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/sigil/custom/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/sigil/regex/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/sigil/string/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_end/sigil/words/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_line_body/quote/PromoterTest.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/group_heredoc_line_body/sigil/PromoterTest.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/named_sigil/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/sigil_modifiers/group/Test.class")
    exclude("**/org/elixir_lang/elixir_flex_lexer/sigil_modifiers/group_heredoc_end/Test.class")
    exclude("**/org/elixir_lang/flex_lexer/Test.class")
    exclude("**/org/elixir_lang/parser_definition/ParsingTestCase.class")
    exclude("**/org/elixir_lang/parser_definition/matched_call_operation/ParsingTestCase.class")
    exclude("**/org/elixir_lang/parser_definition/matched_dot_operator_call_operation/ParsingTestCase.class")
}

// Get the list of platforms from gradle.properties
val runIdePlatformsList = providers.gradleProperty("runIdePlatforms").get().split(",")

intellijPlatformTesting {
    runIde {
        runIdePlatformsList.forEach { platform ->
            create("run${platform}") {
                type = IntelliJPlatformType.valueOf(platform)
                version = providers.gradleProperty("platformVersion${platform}").get()

                prepareSandboxTask {
                    sandboxDirectory = project.layout.buildDirectory.dir("${platform.lowercase()}-sandbox")
                }
            }

            // if enableEAPIDEs is true, create an EAP instance
            if (providers.gradleProperty("enableEAPIDEs").get().lowercase() == "true") {
                create("run${platform}EAP") {
                    type = IntelliJPlatformType.valueOf(platform)
                    version = providers.gradleProperty("platformVersion${platform}EAP").get()
                    useInstaller = false

                    prepareSandboxTask {
                        sandboxDirectory = project.layout.buildDirectory.dir("${platform.lowercase()}_eap-sandbox")
                    }
                }
            }
        }
    }
}

repositories {
    maven { url = uri("https://maven-central.storage.googleapis.com") }
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(providers.gradleProperty("platformType"), provider { actualPlatformVersion })

        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(",").toList() })
        bundledModules(providers.gradleProperty("platformBundledModules").map { it.split(",").toList() })
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    implementation(project(":jps-builder"))
    implementation(project(":jps-shared"))
    implementation(files("lib/OtpErlang.jar"))
    implementation(group = "commons-io", name = "commons-io", version = "2.21.0")
}

tasks.named("compileJava") {
    dependsOn(":jps-shared:composedJar")
    dependsOn(":jps-builder:composedJar")
}

apply(plugin = "idea")
configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
    project {
        jdkName = project.property("javaVersion") as String
        languageLevel = org.gradle.plugins.ide.idea.model.IdeaLanguageLevel(project.property("javaVersion") as String)
    }
    module {
        generatedSourceDirs.add(file("gen"))
    }
}

val elixirZipFile = file("${rootDir}/cache/Elixir.${elixirVersion}.zip")

val downloadElixir = tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadElixir") {
    src("https://github.com/elixir-lang/elixir/archive/v${elixirVersion}.zip")
    dest(elixirZipFile)
    overwrite(false)
}

val unzipElixir = tasks.register<Copy>("unzipElixir") {
    dependsOn(downloadElixir)
    from(zipTree(elixirZipFile))
    into(elixirPath)
    eachFile {
        relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
    }
    // Only include files from within the expected root directory.
    include("elixir-${elixirVersion}/**")

    inputs.file(elixirZipFile)
    outputs.dir(elixirPath)
}

val buildElixir = tasks.register<Exec>("buildElixir") {
    dependsOn(unzipElixir)
    workingDir(elixirPath)
    commandLine("make")
    inputs.dir(elixirPath)
    outputs.dir(file("${elixirPath}/bin"))
}

tasks.register("getElixir") {
    dependsOn(buildElixir)
}

tasks.register<de.undercouch.gradle.tasks.download.Download>("getQuoter") {
    src("https://github.com/KronicDeth/intellij_elixir/archive/v${quoterVersion}.zip")
    dest(quoterZipPath)
    overwrite(false)

    doLast {
        val folder = file(quoterUnzippedPath)
        if (!folder.isDirectory || folder.list()?.isEmpty() != false) {
            copy {
                from(zipTree(quoterZipPath))
                into(cachePath)
            }

            val quoterZipRootFile = file(quoterZipRootPath)
            quoterZipRootFile.renameTo(file(quoterUnzippedPath))
        }
    }
}

tasks.register<Exec>("getQuoterDeps") {
    dependsOn("getQuoter")
    workingDir(quoterUnzippedPath)
    commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get")
}

tasks.register<Exec>("releaseQuoter") {
    dependsOn("getQuoterDeps")
    workingDir(quoterUnzippedPath)
    commandLine("mix", "do", "local.rebar", "--force,", "local.hex", "--force,", "deps.get,", "release")
    onlyIf { !file(quoterExe).canExecute() }
}

tasks.named("compileTestJava") {
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

tasks.named<RunIdeTask>("runIde") {
    systemProperty("idea.log.debug.categories", "org.elixir_lang=TRACE")
    // When wanting to disable EDT slow assertion.
    // systemProperty("ide.slow.operations.assertion", "true")

    // -Didea.debug.mode=true:
    // This enables debug mode for IntelliJ IDEA. It can be useful when developing plugins to get more detailed logging and debugging information.
    // -Didea.is.internal=true:
    // This flag indicates that the plugin is running in an internal mode, which may enable additional features or logging that are typically only available to IntelliJ developers.
    // -Dlog4j2.debug=true:
    // This enables debug logging for Log4j 2, which is the logging framework used by IntelliJ IDEA. It can help in troubleshooting logging-related issues in your plugin.
    // -Dlogger.org=TRACE:
    // This sets the logging level for the "org" package to TRACE, which is the most verbose logging level. This can be useful for detailed logging of plugin activities.
    // -XX:+AllowEnhancedClassRedefinition:
    // This is a JVM flag that allows for more flexible class redefinition during runtime. It can be beneficial for hot-swapping code changes without restarting the IDE.
    // -Didea.ProcessCanceledException=disabled:
    //This disables the throwing of ProcessCanceledException, which is typically used to cancel long-running processes in IntelliJ IDEA. Disabling it can be useful in certain debugging scenarios.
    jvmArguments.addAll(listOf(
        "-Didea.debug.mode=true",
        "-XX:+AllowEnhancedClassRedefinition",
        "-Didea.is.internal=true",
        "-Dlog4j2.debug=true",
        "-Dlogger.org=TRACE",
        "-Didea.ProcessCanceledException=disabled"
    ))
    maxHeapSize = "7g"
    // get from runIdeWorkingDirectory
    if (project.hasProperty("runIdeWorkingDirectory") && project.property("runIdeWorkingDirectory").toString().isNotEmpty()) {
        workingDir = file(project.property("runIdeWorkingDirectory").toString())
    }
}

tasks.named<Test>("test") {
    dependsOn("prepareTestSandbox", "runQuoter")
    finalizedBy("stopQuoter")
}

// Uncomment to allow using build-scan.
// if (hasProperty("buildScan")) {
//     buildScan {
//         termsOfServiceUrl = "https://gradle.com/terms-of-service"
//         termsOfServiceAgree = "yes"
//     }
// }
