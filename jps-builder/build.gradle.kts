import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    alias(libs.plugins.test.logger)
    id("org.jetbrains.intellij.platform.base")
}

base {
    archivesName.set("${rootProject.name}.${project.name}")
}

sourceSets {
    test {
        java.srcDir("tests")
    }
}

// Java level (source/target compatibility, --release, encoding) is configured by the root
// build script, derived from the target platform (Java 25 for build 262+, otherwise 21).

// Ensuring the necessary tasks are executed before tests
tasks.test {
    // Elixir stdlib ebin comes from the SDK resolved by the root `resolveElixirErlangSdks`
    // (mise/PATH/env aware) - no from-source Elixir build. Resolved paths are known only at
    // execution, so set env in a doFirst reading the resolver's output.
    dependsOn(":resolveElixirErlangSdks")

    useJUnit()
    jvmArgs(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
        "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
        "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED",
        "--add-opens=java.base/java.nio=ALL-UNNAMED",
    )

    val sdkProps = rootProject.layout.buildDirectory.file("elixir-erlang-sdks.properties")
    doFirst {
        environment(sdk.elixirTestEnvironment(sdkProps.get().asFile))
    }

    include("**/*Test.class")

    // Allow the task to succeed when a global --tests filter matches nothing in this subproject
    // (e.g., when running `gradlew test --tests SomeTestInRootProject`)
    filter.isFailOnNoMatchingTests = false
}

configurations {
    named("testRuntimeClasspath") {
        extendsFrom(
            getByName("intellijPlatformTestClasspath"),
            getByName("intellijPlatformTestRuntimeFixClasspath")
        )
    }
}

dependencies {
    intellijPlatform {
        testFramework(TestFrameworkType.Platform)
        bundledPlugins("com.intellij.java")
    }
    implementation(project(":jps-shared"))
    testImplementation(libs.junit)
}
