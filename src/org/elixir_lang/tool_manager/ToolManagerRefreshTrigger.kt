package org.elixir_lang.tool_manager

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import java.nio.file.Path

/**
 * A mechanism by which an [ElixirToolManager] can notify the system when its configuration
 * state may have changed and a re-scan is needed.
 *
 * Each implementation is responsible for installing whatever listeners, watchers, or polling
 * mechanisms it needs and calling `onChangeDetected` when a re-scan should occur.
 *
 * The trigger is re-installed after every completed scan cycle so that dynamically discovered
 * watch targets (e.g. `mise config ls --json`) stay up to date.
 *
 * Implementations are **not** required to be stateless - [install] may perform IO to discover
 * which files or resources to watch.  It is always called on a background thread.
 *
 * Examples of different trigger mechanisms:
 * - **File-based** (mise, asdf, rtx): watch config files for modifications + content-root
 *   directories for new files matching name patterns.
 * - **Process-based**: listen for a specific CLI process to exit.
 * - **Polling**: periodic background check for state changes.
 * - **External service**: listen on a socket or HTTP endpoint.
 */
fun interface ToolManagerRefreshTrigger {

    /**
     * Installs the trigger for the given [contentRoots].
     *
     * Called on a **background IO thread** after each scan cycle completes.
     * May perform IO (e.g. run a CLI command to discover watched files).
     *
     * @param project          The project this trigger belongs to.
     * @param contentRoots     Content roots that were most recently scanned.
     * @param onChangeDetected Called (on any thread) when a re-scan should be triggered.
     *                         Implementations must call this at most once per logical change event;
     *                         the scan service debounces multiple rapid calls automatically.
     * @return A [Disposable] that tears down all listeners/watchers installed by this call.
     *         Disposed either when a new trigger cycle begins or when the project closes.
     */
    fun install(
        project: Project,
        contentRoots: List<Path>,
        onChangeDetected: () -> Unit,
    ): Disposable
}
