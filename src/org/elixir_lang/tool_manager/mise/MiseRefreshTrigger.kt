package org.elixir_lang.tool_manager.mise

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.mise.Mise
import org.elixir_lang.tool_manager.ToolManagerRefreshTrigger
import org.jetbrains.annotations.VisibleForTesting
import java.nio.file.Path

private val LOG = logger<MiseRefreshTrigger>()

/**
 * File-name patterns to watch for in content root directories.
 *
 * These cover all standard mise config file names.  If a file matching any of these
 * patterns is created in a content root directory, a re-scan is triggered even if the
 * file was not previously known to mise.
 */
private val MISE_CONFIG_PATTERNS = listOf(
    Regex("""^mise\.toml$"""),
    Regex("""^mise\..+\.toml$"""),   // mise.local.toml, mise.test.toml, etc.
    Regex("""^\.tool-versions$"""),
)

/**
 * [ToolManagerRefreshTrigger] implementation for mise.
 *
 * On [install]:
 * 1. Calls `mise config ls --json` for each content root to discover the exact set of
 *    config files that mise is currently reading (including user-global config files such
 *    as `~/.config/mise/config.toml`).
 * 2. Registers [LocalFileSystem] watch roots for every discovered file so the VFS tracks
 *    changes to files that may lie outside the project directory.
 * 3. Subscribes a [BulkFileListener] that fires `onChangeDetected` when:
 *    - Any watched exact file is modified or deleted.
 *    - A file matching [MISE_CONFIG_PATTERNS] is created inside a content root directory
 *      (handles the case where the user adds a new config file not yet known to mise).
 */
object MiseRefreshTrigger : ToolManagerRefreshTrigger {

    override fun install(
        project: Project,
        contentRoots: List<Path>,
        onChangeDetected: () -> Unit,
    ): Disposable {
        val lifetime = Disposer.newDisposable("MiseRefreshTrigger")

        // --- 1. Discover exact config files from mise for each content root ---
        // Mise.configFiles() already converts WSL Linux paths to Windows UNC paths (see Mise.kt),
        // so Path objects here are always host-native absolute paths.
        // Normalise to forward slashes so strings match VFileEvent.path values.
        val exactPaths: Set<String> = contentRoots
            .flatMap { root ->
                Mise.configFiles(root)
                    ?.map { FileUtil.toSystemIndependentName(it.toString()) }
                    ?.also { LOG.trace("install: $root → ${it.size} config file(s)") }
                    ?: emptyList()
            }
            .toSet()

        LOG.debug("install: watching ${exactPaths.size} exact file(s) across ${contentRoots.size} content root(s)")

        // --- 2. Register LocalFileSystem watch roots ---
        // Watch each exact file individually (handles files outside the project, e.g. ~/.config/mise/config.toml).
        // Also watch each content root directory (non-recursive) so new files can be detected.
        // Both exactPaths and content root strings are in system-independent (forward-slash) form,
        // which LocalFileSystem normalises internally.
        val watchPaths: Set<String> = exactPaths + contentRoots.map { FileUtil.toSystemIndependentName(it.toString()) }
        val watchRequests = mutableSetOf<LocalFileSystem.WatchRequest>()
        val lfs = LocalFileSystem.getInstance()
        for (path in watchPaths) {
            lfs.addRootToWatch(path, /* watchRecursively = */ false)
                ?.let { watchRequests.add(it) }
        }

        // Unregister all watch requests when the trigger is disposed.
        Disposer.register(lifetime) {
            lfs.removeWatchedRoots(watchRequests)
            LOG.trace("install: removed ${watchRequests.size} watch request(s)")
        }

        // Normalise content root strings to forward slashes to match VFileCreateEvent.parent.path.
        val contentRootStrings: Set<String> = contentRoots.map { FileUtil.toSystemIndependentName(it.toString()) }.toSet()

        // --- 3. Subscribe BulkFileListener for VFS change events ---
        ApplicationManager.getApplication().messageBus
            .connect(lifetime)
            .subscribe(
                com.intellij.openapi.vfs.VirtualFileManager.VFS_CHANGES,
                object : BulkFileListener {
                    override fun after(events: List<VFileEvent>) {
                        for (event in events) {
                            if (shouldTrigger(event, exactPaths, contentRootStrings)) {
                                LOG.debug("install: change detected via ${event::class.simpleName} on '${event.path}', triggering re-scan")
                                onChangeDetected()
                                return  // one call per batch is sufficient; scan is debounced
                            }
                        }
                    }
                }
            )

        return lifetime
    }

    @VisibleForTesting
    internal fun shouldTrigger(
        event: VFileEvent,
        exactPaths: Set<String>,
        contentRootStrings: Set<String>,
    ): Boolean = when (event) {
        // Exact watched file was modified.
        is VFileContentChangeEvent -> event.path in exactPaths

        // Exact watched file was deleted - re-scan so mise can report absence.
        is VFileDeleteEvent -> event.path in exactPaths

        // A new file was created in a content root - check against name patterns.
        is VFileCreateEvent -> {
            val parentPath = event.parent.path
            if (parentPath in contentRootStrings) {
                MISE_CONFIG_PATTERNS.any { it.matches(event.childName) }
            } else {
                false
            }
        }

        else -> false
    }
}
