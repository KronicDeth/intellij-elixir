import org.gradle.api.tasks.testing.logging.TestExceptionFormat

tasks.jar {
    archiveFileName.set("jps-builder.jar")
}

tasks.compileTestJava {
    dependsOn(":jps-shared:composedJar")
}

tasks.compileJava {
    dependsOn(":jps-shared:composedJar")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// Ensuring the necessary tasks are executed before tests
tasks.test {
    dependsOn(":getElixir")

    val elixirPath: String by project
    val elixirVersion: String by project

    environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
    environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
    environment("ELIXIR_VERSION", elixirVersion)

    include("**/*Test.class")

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

dependencies {
    implementation(project(":jps-shared"))
}
