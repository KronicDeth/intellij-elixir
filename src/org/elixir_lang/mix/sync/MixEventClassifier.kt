package org.elixir_lang.mix.sync

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.isElixirMixModule
import org.elixir_lang.mixContentRoots
import org.elixir_lang.package_manager.virtualFile

/**
 * Shared root-detection layer for Mix VFS event classification.
 *
 * Determines **which top-level Mix content roots are touched** by a set of VFS events.
 * Intentionally limited to root-detection: given a batch of VFS events, answer "which
 * project roots need to act?" without deciding *what* action to take.
 *
 * The fine-grained structural matching (e.g. `_build/<env>/lib/<dep>/ebin` → `deps/<dep>`,
 * delete-vs-sync classification) stays with each listener adapter because that logic differs
 * per listener and must not be conflated with this coarse root filter.
 *
 * Kept as a standalone object so that any listener or service that watches Mix VFS paths can
 * reuse the same root-detection logic without duplicating the module-to-content-root mapping.
 */
object MixEventClassifier {

    /**
     * Returns the subset of top-level Elixir Mix content roots that are touched by [events].
     *
     * A root is "touched" when at least one event path is under `<root>/deps`,
     * `<root>/_build`, equals `<root>/mix.exs`, or equals `<root>/mix.lock`.
     *
     * This is O(events × roots). Non-Elixir modules are excluded before any path comparison
     * via [isElixirMixModule].
     *
     * Returns an empty set if [project] is disposed.
     */
    fun findAffectedMixRoots(project: Project, events: List<VFileEvent>): Set<VirtualFile> {
        if (project.isDisposed) return emptySet()
        val eventPaths = events.map { FileUtil.toSystemIndependentName(it.path) }
        val candidateRoots = elixirMixContentRoots(project)
        return findAffectedMixRoots(candidateRoots, eventPaths)
    }

    /**
     * Filters [candidateRoots] to those touched by at least one path in [eventPaths].
     *
     * Extracted as a separate function so the pure path-filtering logic can be tested
     * independently of IntelliJ module discovery, which requires a fully configured project
     * with registered Elixir modules and content roots.
     */
    internal fun findAffectedMixRoots(
        candidateRoots: List<VirtualFile>,
        eventPaths: List<String>,
    ): Set<VirtualFile> {
        return candidateRoots.filter { root ->
            val rootPath = FileUtil.toSystemIndependentName(root.path)
            eventPaths.any { eventPath -> isDepsPathForRoot(eventPath, rootPath) }
        }.toSet()
    }

    /**
     * Top-level Mix content roots from modules that pass [isElixirMixModule].
     *
     * Non-Elixir modules are excluded before any path comparison happens.
     *
     * Returns an empty list if [project] is disposed.
     */
    fun elixirMixContentRoots(project: Project): List<VirtualFile> {
        if (project.isDisposed) return emptyList()
        val allMixRoots = ModuleManager.getInstance(project).modules
            .filter { it.isElixirMixModule() }
            .flatMap { module -> module.mixContentRoots().map { it.root } }
        return selectTopLevelMixRoots(allMixRoots)
    }

    /**
     * Returns true if [eventPath] falls under one of the paths that signal a Mix deps change
     * relative to [rootPath]:
     * - `<rootPath>/deps/` (any descendant)
     * - `<rootPath>/_build/` (any descendant)
     * - `<rootPath>/mix.exs` (exact)
     * - `<rootPath>/mix.lock` (exact)
     *
     * Note: this is a **coarse boolean** for root-scoping only. It is NOT equivalent to the
     * fine-grained `SyncRequest` type classification performed by dep watcher adapters.
     */
    fun isDepsPathForRoot(eventPath: String, rootPath: String): Boolean =
        FileUtil.isAncestor("$rootPath/deps", eventPath, false) ||
            FileUtil.isAncestor("$rootPath/_build", eventPath, false) ||
            FileUtil.pathsEqual(eventPath, "$rootPath/mix.exs") ||
            FileUtil.pathsEqual(eventPath, "$rootPath/mix.lock")

    /**
     * Given a list of Mix roots, returns only those that are not nested inside another root in
     * the same list.
     *
     * This filters out umbrella app sub-roots when the umbrella root itself is present, so that
     * checks are triggered at the umbrella level only.
     *
     * A root is included if [virtualFile][org.elixir_lang.package_manager.virtualFile] returns
     * non-null for it (i.e. it has a `mix.exs`).
     */
    fun selectTopLevelMixRoots(roots: List<VirtualFile>): List<VirtualFile> {
        val mixRoots = roots.filter { virtualFile(it) != null }
        if (mixRoots.size <= 1) return mixRoots

        val mixRootPaths = mixRoots.map { FileUtil.toSystemIndependentName(it.path) }
        return mixRoots.filter { root ->
            val rootPath = FileUtil.toSystemIndependentName(root.path)
            mixRootPaths.none { otherPath ->
                otherPath != rootPath && FileUtil.isAncestor(otherPath, rootPath, false)
            }
        }
    }

    /**
     * Array-accepting overload of [selectTopLevelMixRoots] for call sites using
     * [com.intellij.openapi.roots.ProjectRootManager.contentRootsFromAllModules].
     */
    fun selectTopLevelMixRoots(roots: Array<out VirtualFile>): List<VirtualFile> =
        selectTopLevelMixRoots(roots.asList())
}
