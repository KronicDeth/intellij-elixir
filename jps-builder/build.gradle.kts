import org.jetbrains.intellij.platform.gradle.TestFrameworkType

base {
    archivesName.set("${rootProject.name}.${project.name}")
}

sourceSets {
    test {
        java.srcDir("tests")
    }
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

    environment("ELIXIR_LANG_ELIXIR_PATH", elixirPath)
    environment("ELIXIR_EBIN_DIRECTORY", "${elixirPath}/lib/elixir/ebin/")
    environment("ELIXIR_VERSION", elixirVersion)

    include("**/*Test.class")
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
