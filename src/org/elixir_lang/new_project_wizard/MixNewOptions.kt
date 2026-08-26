package org.elixir_lang.new_project_wizard

import java.nio.file.Paths

/**
 * The `mix new` arguments and the module layout they imply, extracted from [Step.setupProject] so
 * they can be asserted without running `mix new`.
 *
 * `--umbrella` selects a different kind of project rather than modifying one: the root declares
 * `apps_path:` instead of `app:`, so it has no `lib/` and compiles nothing of its own, and each
 * child application builds to `_build/dev/lib/<child>/ebin` instead.
 */
internal object MixNewOptions {
    /**
     * `mix new` ignores `--sup` at an umbrella root and reports nothing, so it is dropped here
     * rather than passed and silently lost.
     */
    fun parameters(
        projectDirectory: String,
        app: String,
        module: String,
        sup: Boolean,
        umbrella: Boolean
    ): List<String> = buildList {
        add("new")
        add(projectDirectory)

        if (app.isNotBlank()) {
            add("--app")
            add(app)
        }

        if (module.isNotBlank()) {
            add("--module")
            add(module)
        }

        if (sup && !umbrella) {
            add("--sup")
        }

        if (umbrella) {
            add("--umbrella")
        }
    }

    /**
     * `null` for an umbrella root, which `mix new` leaves without a `lib/`.
     * [com.intellij.ide.util.projectWizard.JavaModuleBuilder] creates every source path it is
     * given, so passing one would leave a stray empty directory behind.
     */
    fun sourcePath(projectDirectory: String, umbrella: Boolean): String? =
        if (umbrella) {
            null
        } else {
            Paths.get(projectDirectory, "lib").toString()
        }

    /**
     * `null` for an umbrella root, which produces no `.beam` of its own, so that
     * [com.intellij.ide.util.projectWizard.JavaModuleBuilder] inherits the compiler output path
     * rather than pointing the module at a directory that never exists.
     *
     * [app] is blank whenever `--app` is left unset, which the wizard permits because `mix new`
     * then infers the application name from the last segment of the path - that is [name].
     */
    fun compilerOutputPath(projectDirectory: String, name: String, app: String, umbrella: Boolean): String? =
        if (umbrella) {
            null
        } else {
            Paths.get(projectDirectory, "_build", "dev", "lib", app.ifBlank { name }, "ebin").toString()
        }
}
