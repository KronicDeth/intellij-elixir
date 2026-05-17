package org.elixir_lang.mix.sync

import com.google.common.annotations.VisibleForTesting
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.mix.Project as MixProject

/**
 * A classified request produced from a single VFS event by [classifyVfsEvent].
 *
 * The sealed class covers all event types that [MixDepsBulkFileListener] needs to act on.
 * Listener-produced requests are deliberately path-shaped and unresolved: the listener does cheap
 * path inspection only, while [MixDepsSyncService] resolves modules/content roots later under read
 * access. All requests carry [requestedAtNs] so the service layer can log queue-wait latency
 * without an additional model change later.
 *
 * Coalescing rules (enforced by the service drain):
 * - [All] supersedes any pending [DepsRoot] or [DepRoot] requests.
 * - [DeleteAll] supersedes any pending [DepsRoot] or [DepRoot] requests for the same tree.
 * - Deletes drain before syncs to preserve the ordering of the existing `flushPending()` semantics.
 */
sealed class SyncRequest(
    /** Nanosecond timestamp captured at classification time, used for queue-wait latency logging. */
    val requestedAtNs: Long = System.nanoTime()
) {
    /** Sync all deps across all content roots (e.g. `_build` structural change). */
    object All : SyncRequest()

    /** Sync all deps under a single `deps/` directory (e.g. the `deps/` dir itself was created). */
    data class DepsRoot(val depsRoot: VirtualFile) : SyncRequest()

    /**
     * Sync a single dep directory.
     *
     * Produced when a source subdirectory (`lib`, `src`, `priv`, `c_src`) under `deps/<dep>` changes,
     * or when `_build/<env>/lib/<dep>` or `_build/<env>/lib/<dep>/ebin` changes (the ebin path is
     * resolved back to the corresponding `deps/<dep>` VirtualFile).
     */
    data class DepRoot(val depRoot: VirtualFile) : SyncRequest()

    /**
     * Unresolved `_build` path that maps to a full dep sync if [contentRootCandidate] is an actual
     * module content root. The service validates that under read access before converting to [All].
     */
    data class BuildPath(val contentRootCandidate: VirtualFile) : SyncRequest()

    /**
     * Unresolved `_build/<env>/lib/<dep>` or `_build/<env>/lib/<dep>/ebin` path.
     *
     * The service validates [contentRootCandidate] and resolves [depName] to `deps/<dep>` later.
     */
    data class BuildDep(val contentRootCandidate: VirtualFile, val depName: String) : SyncRequest()

    /** Delete all dep libraries whose source roots originate under the given `deps/` URL. */
    data class DeleteAll(val depsUrl: String, val contentRootUrl: String? = null) : SyncRequest()

    /** Delete the single project library named [depName]. */
    data class DeleteOne(
        val depName: String,
        val depsUrl: String? = null,
        val contentRootUrl: String? = null,
    ) : SyncRequest()

    /**
     * Unresolved `mix.exs` content-change request.
     *
     * Produced by [MixDepsBulkFileListener]. The service resolves this to [SyncModule] only if the
     * file is directly under a module content root.
     */
    data class MixFile(val mixFile: VirtualFile) : SyncRequest()

    /**
     * Re-resolve and sync the library/module dependencies for a single [Module].
     *
     * Kept for direct service callers and tests. Listener-side classification produces [MixFile]
     * instead, and the service resolves it during drain.
     */
    data class SyncModule(val moduleName: String) : SyncRequest() {
        constructor(module: Module) : this(module.name)
    }
}

private val SOURCE_NAMES = setOf("c_src", "lib", "priv", "src")

/**
 * Classifies a single VFS event into a [SyncRequest], or returns `null` if the event does not
 * affect Mix deps or build output.
 *
 * This is the dep-watcher-specific layer-2 classifier. It produces fine-grained, path-shaped
 * [SyncRequest] instances from structural path matching. It is intentionally separate from
 * [MixEventClassifier.isDepsPathForRoot], which is a coarser boolean used only for root-scoping.
 *
 * The mapping mirrors the legacy dep-watcher event-dispatch logic, but avoids project-model
 * lookups. [MixDepsSyncService] validates module/content-root ownership later under read access.
 *
 * Delete events:
 * - `deps/` → [SyncRequest.DeleteAll] with the deleted dir's URL and parent URL candidate
 * - `deps/<dep>` → [SyncRequest.DeleteOne] with the dep name and parent URL candidates
 * - anything else that matches the path rules below → sync request
 *
 * Create/change events (and non-delete events routed through the path rules):
 * - `mix.exs` content change → [SyncRequest.MixFile]
 * - `_build` → [SyncRequest.BuildPath]
 * - `_build/<env>` → [SyncRequest.BuildPath]
 * - `_build/<env>/consolidated` or `_build/<env>/lib` → [SyncRequest.BuildPath]
 * - `deps/<dep>/lib|src|priv|c_src` → [SyncRequest.DepRoot] (the `deps/<dep>` dir)
 * - `_build/<env>/lib/<dep>` → [SyncRequest.BuildDep]
 * - `_build/<env>/lib/<dep>/ebin` → [SyncRequest.BuildDep]
 * - `deps/` → [SyncRequest.DepsRoot]
 * - `deps/<dep>` → [SyncRequest.DepRoot]
 * - anything else → `null`
 */
fun classifyVfsEvent(event: VFileEvent): SyncRequest? =
    when (event) {
        is VFileDeleteEvent -> classifyDeleteEvent(event)
        is VFileCreateEvent -> classifyCreateEvent(event)
        is VFileContentChangeEvent ->
            if (event.file.name == MixProject.MIX_EXS) SyncRequest.MixFile(event.file)
            else classifyByPath(event.file)
        else -> classifyByPath(event.file ?: return null)
    }

private fun classifyDeleteEvent(event: VFileDeleteEvent): SyncRequest? {
    val file = event.file
    return when {
        file.name == "deps" ->
            SyncRequest.DeleteAll(file.url, file.parent?.url)
        file.parent?.name == "deps" ->
            SyncRequest.DeleteOne(file.name, file.parent?.url, file.parent?.parent?.url)
        else ->
            classifyByPath(file)
    }
}

private fun classifyCreateEvent(event: VFileCreateEvent): SyncRequest? {
    val file = event.file ?: return null
    return when {
        file.name == "deps" ->
            SyncRequest.DepsRoot(file)
        event.parent.name == "deps" ->
            SyncRequest.DepRoot(file)
        else ->
            classifyByPath(file)
    }
}

/**
 * Path-based classification shared by create, change, and fall-through delete events.
 *
 * Performs only cheap path-shape checks. It does not call project-model APIs; the service validates
 * whether candidate content roots are real module content roots during drain.
 */
@VisibleForTesting
internal fun classifyByPath(file: VirtualFile): SyncRequest? {
    val fileName = file.name
    val parent = file.parent ?: return null

    // _build
    if (fileName == "_build") {
        return SyncRequest.BuildPath(parent)
    }

    val grandParent = parent.parent ?: return null

    // _build/<env>
    if (parent.name == "_build") {
        return SyncRequest.BuildPath(grandParent)
    }

    val greatGrandParent = grandParent.parent ?: return null

    // _build/<env>/consolidated  or  _build/<env>/lib
    if (fileName in arrayOf("consolidated", "lib") && grandParent.name == "_build") {
        return SyncRequest.BuildPath(greatGrandParent)
    }

    // deps/<dep>/lib|src|priv|c_src
    if (fileName in SOURCE_NAMES && grandParent.name == "deps") {
        return SyncRequest.DepRoot(parent)
    }

    val greatGreatGrandParent = greatGrandParent.parent ?: return null

    // _build/<env>/lib/<dep>
    if (parent.name == "lib" && greatGrandParent.name == "_build") {
        return SyncRequest.BuildDep(greatGreatGrandParent, fileName)
    }

    // _build/<env>/lib/<dep>/ebin
    if (fileName == "ebin" &&
        grandParent.name == "lib" &&
        greatGreatGrandParent.name == "_build"
    ) {
        return SyncRequest.BuildDep(greatGreatGrandParent.parent ?: return null, parent.name)
    }

    return null
}
