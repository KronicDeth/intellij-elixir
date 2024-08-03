dependencies {
    implementation(project(":jps-shared"))
}

tasks.named("compileTestJava") {
    dependsOn(":jps-shared:composedJar")
}
tasks.named("compileJava") {
    dependsOn(":jps-shared:composedJar")
}

tasks.named<Jar>("jar") {
    archiveFileName.set("jps-builder.jar")
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