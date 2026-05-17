package org.elixir_lang.mix.sync

import com.google.common.annotations.VisibleForTesting
import com.intellij.ide.projectView.impl.ProjectRootsUtil.isModuleContentRoot
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

/**
 * A classified request produced from a single VFS event by [classifyVfsEvent].
 *
 * The sealed class covers all event types that [org.elixir_lang.DepsWatcher] and
 * [org.elixir_lang.mix.Watcher] need to act on - syncs of varying granularity and two delete
 * variants. All requests carry [requestedAtNs] so the service layer can log queue-wait latency
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

    /** Delete all dep libraries whose source roots originate under the given `deps/` URL. */
    data class DeleteAll(val depsUrl: String) : SyncRequest()

    /** Delete the single project library named [depName]. */
    data class DeleteOne(val depName: String) : SyncRequest()

    /**
     * Re-resolve and sync the library/module dependencies for a single [Module].
     *
     * Produced by [org.elixir_lang.mix.Watcher] when a `mix.exs` file inside one of the module's
     * content roots is saved. The service runs a transitive PSI resolution on the module's
     * `mix.exs` and updates module-level dependency entries accordingly.
     */
    data class SyncModule(val module: Module) : SyncRequest()
}

private val SOURCE_NAMES = setOf("c_src", "lib", "priv", "src")

/**
 * Classifies a single VFS event into a [SyncRequest], or returns `null` if the event does not
 * affect Mix deps or build output.
 *
 * This is the dep-watcher-specific layer-2 classifier. It produces fine-grained [SyncRequest]
 * instances from structural path matching. It is intentionally separate from
 * [MixEventClassifier.isDepsPathForRoot], which is a coarser boolean used only for root-scoping.
 *
 * The mapping mirrors [org.elixir_lang.DepsWatcher]'s existing event-dispatch logic:
 *
 * Delete events:
 * - `deps/` (content root child) → [SyncRequest.DeleteAll] with the deleted dir's URL
 * - `deps/<dep>` (content root grandchild) → [SyncRequest.DeleteOne] with the dep name
 * - anything else that matches the path rules below → sync request
 *
 * Create/change events (and non-delete events routed through the path rules):
 * - `_build` (content root child) → [SyncRequest.All]
 * - `_build/<env>` → [SyncRequest.All]
 * - `_build/<env>/consolidated` or `_build/<env>/lib` → [SyncRequest.All]
 * - `deps/<dep>/lib|src|priv|c_src` → [SyncRequest.DepRoot] (the `deps/<dep>` dir)
 * - `_build/<env>/lib/<dep>` → [SyncRequest.DepRoot] (resolved to `deps/<dep>`)
 * - `_build/<env>/lib/<dep>/ebin` → [SyncRequest.DepRoot] (resolved to `deps/<dep>`)
 * - `deps/` (the deps dir itself, created) → [SyncRequest.DepsRoot]
 * - `deps/<dep>` (created directly) → [SyncRequest.DepRoot]
 * - anything else → `null`
 */
fun classifyVfsEvent(project: Project, event: VFileEvent): SyncRequest? =
    when (event) {
        is VFileDeleteEvent -> classifyDeleteEvent(project, event)
        is VFileCreateEvent -> classifyCreateEvent(project, event)
        else -> classifyByPath(project, event.file ?: return null)
    }

private fun classifyDeleteEvent(project: Project, event: VFileDeleteEvent): SyncRequest? {
    val file = event.file
    return when {
        file.name == "deps" && isModuleContentRoot(file.parent, project) ->
            SyncRequest.DeleteAll(file.url)
        file.parent?.let { parent ->
            parent.name == "deps" && isModuleContentRoot(parent.parent, project)
        } == true ->
            SyncRequest.DeleteOne(file.name)
        else ->
            classifyByPath(project, file)
    }
}

private fun classifyCreateEvent(project: Project, event: VFileCreateEvent): SyncRequest? {
    val file = event.file ?: return null
    return when {
        file.name == "deps" && isModuleContentRoot(event.parent, project) ->
            SyncRequest.DepsRoot(file)
        event.parent.let { parent ->
            parent.name == "deps" && isModuleContentRoot(parent.parent, project)
        } ->
            SyncRequest.DepRoot(file)
        else ->
            classifyByPath(project, file)
    }
}

/**
 * Path-based classification shared by create, change, and fall-through delete events.
 *
 * Implements the same ancestor-walk logic as [org.elixir_lang.DepsWatcher] but returns
 * the expanded [SyncRequest] type.
 */
@VisibleForTesting
internal fun classifyByPath(project: Project, file: VirtualFile): SyncRequest? {
    val fileName = file.name
    val parent = file.parent ?: return null

    // _build  (content-root child)
    if (fileName == "_build" && isModuleContentRoot(parent, project)) {
        return SyncRequest.All
    }

    val grandParent = parent.parent ?: return null

    // _build/<env>  (content-root grandchild)
    if (parent.name == "_build" && isModuleContentRoot(grandParent, project)) {
        return SyncRequest.All
    }

    val greatGrandParent = grandParent.parent ?: return null

    // _build/<env>/consolidated  or  _build/<env>/lib
    if (fileName in arrayOf("consolidated", "lib") &&
        grandParent.name == "_build" &&
        isModuleContentRoot(greatGrandParent, project)
    ) {
        return SyncRequest.All
    }

    // deps/<dep>/lib|src|priv|c_src
    if (fileName in SOURCE_NAMES &&
        grandParent.name == "deps" &&
        isModuleContentRoot(greatGrandParent, project)
    ) {
        return SyncRequest.DepRoot(parent)
    }

    val greatGreatGrandParent = greatGrandParent.parent ?: return null

    // _build/<env>/lib/<dep>
    if (parent.name == "lib" &&
        greatGrandParent.name == "_build" &&
        isModuleContentRoot(greatGreatGrandParent, project)
    ) {
        val dep = greatGreatGrandParent.findChild("deps")?.findChild(fileName)
        return dep?.let { SyncRequest.DepRoot(it) }
    }

    // _build/<env>/lib/<dep>/ebin
    if (fileName == "ebin" &&
        grandParent.name == "lib" &&
        greatGreatGrandParent.name == "_build" &&
        isModuleContentRoot(greatGreatGrandParent.parent ?: return null, project)
    ) {
        val dep = greatGreatGrandParent.parent?.findChild("deps")?.findChild(parent.name)
        return dep?.let { SyncRequest.DepRoot(it) }
    }

    return null
}
