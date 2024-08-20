plugins {
    java
}

tasks.jar {
    archiveFileName.set("jps-builder.jar")
}

val compilationPackages = listOf("org/intellij/elixir/build/**", "org/intellij/elixir/jps/**")

tasks.compileTestJava {
    dependsOn(":jps-shared:composedJar")
}

tasks.compileJava {
    dependsOn(":jps-shared:composedJar")
}

tasks.test {
    dependsOn(":getElixir")
    dependsOn(":getQuoter")
    dependsOn(":runQuoter")

    environment("ELIXIR_LANG_ELIXIR_PATH", project.rootProject.extra["elixirPath"] as String)
    environment("ELIXIR_EBIN_DIRECTORY", "${project.rootProject.extra["elixirPath"]}/lib/elixir/ebin/")
    environment("ELIXIR_VERSION", project.rootProject.extra["elixirVersion"] as String)
    useJUnit {
        exclude(compilationPackages)
    }
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    finalizedBy(":stopQuoter")
}

dependencies {
    implementation(project(":jps-shared"))
}