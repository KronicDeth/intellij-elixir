import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    java
}

tasks.named<Jar>("jar") {
    archiveFileName.set("jps-builder.jar")
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
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

val compilationPackages = listOf("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")
subprojects {
    apply(plugin = "org.jetbrains.intellij.platform")
}
tasks.test {
    environment("ELIXIR_LANG_ELIXIR_PATH", rootProject.extra["elixirPath"] as String)
    environment("ELIXIR_EBIN_DIRECTORY", "${rootProject.extra["elixirPath"]}/lib/elixir/ebin/")
    environment("ELIXIR_VERSION", rootProject.extra["elixirVersion"] as String)
    useJUnit {
        exclude(compilationPackages)
    }
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

//tasks.named("publishPlugin") {
//    enabled = false
//}
//
//tasks.named("verifyPlugin") {
//    enabled = false
//}