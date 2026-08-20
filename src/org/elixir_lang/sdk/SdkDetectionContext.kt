package org.elixir_lang.sdk

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.toNioPathOrNull
import java.nio.file.InvalidPathException
import java.nio.file.Path

/**
 * Carries the target directory for SDK detection and creation in wizard flows that have no real
 * [Project] to derive it from.
 *
 * During "Import project from existing sources" and the New Project wizard, the platform hands
 * SDK machinery the *default* project (WizardContext.project is null), so anything that keys the
 * execution environment off the project - SdkDetector's `SdkType.suggestHomePaths(Project)`, the
 * SDK home chooser's base path - silently anchors to the local environment and WSL locations are
 * never scanned or offered (https://youtrack.jetbrains.com/issue/IJPL-243914). Both ends of those
 * chains are plugin code, so the wizard step publishes the import/new-project directory here and
 * the SDK types consult it as a fallback when no usable project is available.
 *
 * The wizard UI writes on the EDT and detection reads on a background thread, hence [Volatile].
 * Wizard steps should [set] in their update hook and [clear] when their UI is disposed; a stale
 * value only ever influences which directories are *scanned* for suggestions, never validation.
 */
object SdkDetectionContext {
    @Volatile
    private var contextPath: Path? = null

    /**
     * The directory to anchor SDK detection and home selection to: the project's own directory
     * when [project] is a real project, otherwise the published wizard directory (or null when
     * neither is available).
     */
    fun resolve(project: Project?): Path? =
        project?.takeUnless { it.isDefault }?.guessProjectDir()?.toNioPathOrNull() ?: contextPath

    /** [resolve] with no project at all - just the published wizard directory. */
    fun resolve(): Path? = contextPath

    fun set(directory: String?) {
        contextPath = directory?.let {
            try {
                Path.of(it)
            } catch (_: InvalidPathException) {
                null
            }
        }
    }

    fun clear() {
        contextPath = null
    }
}
