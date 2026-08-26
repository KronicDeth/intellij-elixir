package org.elixir_lang.new_project_wizard

import junit.framework.TestCase
import java.nio.file.Paths

/**
 * Pins the `mix new` arguments and the module layout they imply, in particular the two shapes that
 * differ: a single application, and an umbrella root that has no application of its own.
 */
class MixNewOptionsTest : TestCase() {
    private val projectDirectory = Paths.get("home", "developer", "my_app").toString()

    private fun path(vararg more: String): String = Paths.get(projectDirectory, *more).toString()

    fun testParametersWithNoOptionsSet() {
        assertEquals(
            listOf("new", projectDirectory),
            MixNewOptions.parameters(projectDirectory, app = "", module = "", sup = false, umbrella = false)
        )
    }

    fun testParametersPassesEverySetOption() {
        assertEquals(
            listOf("new", projectDirectory, "--app", "other_app", "--module", "OtherModule", "--sup"),
            MixNewOptions.parameters(
                projectDirectory,
                app = "other_app",
                module = "OtherModule",
                sup = true,
                umbrella = false
            )
        )
    }

    fun testParametersDropsSupForAnUmbrella() {
        assertEquals(
            "mix new ignores --sup at an umbrella root without reporting it, so it must not be sent",
            listOf("new", projectDirectory, "--umbrella"),
            MixNewOptions.parameters(projectDirectory, app = "", module = "", sup = true, umbrella = true)
        )
    }

    fun testSourcePathIsLibForAnApplication() {
        assertEquals(path("lib"), MixNewOptions.sourcePath(projectDirectory, umbrella = false))
    }

    fun testSourcePathIsAbsentForAnUmbrella() {
        assertNull(
            "an umbrella root has no lib/, and JavaModuleBuilder creates every source path it is given",
            MixNewOptions.sourcePath(projectDirectory, umbrella = true)
        )
    }

    fun testCompilerOutputPathUsesTheApplicationName() {
        assertEquals(
            path("_build", "dev", "lib", "other_app", "ebin"),
            MixNewOptions.compilerOutputPath(projectDirectory, name = "my_app", app = "other_app", umbrella = false)
        )
    }

    fun testCompilerOutputPathFallsBackToTheProjectNameWhenAppIsBlank() {
        assertEquals(
            "a blank --app means mix infers the application name from the path, which is the project name",
            path("_build", "dev", "lib", "my_app", "ebin"),
            MixNewOptions.compilerOutputPath(projectDirectory, name = "my_app", app = "", umbrella = false)
        )
    }

    fun testCompilerOutputPathIsAbsentForAnUmbrella() {
        assertNull(
            "an umbrella root compiles nothing; its children build to _build/dev/lib/<child>/ebin",
            MixNewOptions.compilerOutputPath(projectDirectory, name = "my_app", app = "other_app", umbrella = true)
        )
    }
}
