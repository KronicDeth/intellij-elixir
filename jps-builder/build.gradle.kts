import org.gradle.api.tasks.testing.logging.TestExceptionFormat

dependencies {
    implementation(project(":jps-shared"))
}

plugins {
    id("java")
    id("org.jetbrains.intellij.platform")
}
tasks.named("compileTestJava") {
    dependsOn(":jps-shared:composedJar")
}
tasks.named("compileJava") {
    dependsOn(":jps-shared:composedJar")
}


tasks.jar {
    archiveFileName.set("jps-builder.jar")
}

repositories {
    mavenCentral()
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

// Retrieve elixirPath and elixirVersion from the root project extra properties
val elixirPath: String by rootProject.extra
val elixirVersion: String by rootProject.extra

val compilationPackages = listOf("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")

tasks.test {
    environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
    environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
    environment("ELIXIR_VERSION", elixirVersion)
    useJUnit {
        exclude(compilationPackages)
    }
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks {
    buildSearchableOptions {
        enabled = false
    }
    verifyPlugin {
        enabled = false
    }
    publishPlugin {
        enabled = false
    }
}