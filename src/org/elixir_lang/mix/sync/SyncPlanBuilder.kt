package org.elixir_lang.mix.sync

import com.intellij.openapi.application.readAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresReadLock
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.elixir_lang.mix.Dep
import org.elixir_lang.mix.watcher.TransitiveResolution.transitiveResolution
import org.elixir_lang.mix.Project as MixProject

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------

/** Maximum depth for the ancestor-deps walk in [findAncestorDeps]. */
private const val MAX_ANCESTOR_WALK_DEPTH = 10

/**
 * Source directory names that are included as source roots in a Mix dep's [LibraryRootsPlan].
 * Defined once here as the single source of truth; [org.elixir_lang.mix.sync.SyncRequest]
 * classifyByPath uses the same set via [MIX_DEP_SOURCE_DIR_NAMES].
 */
internal val MIX_DEP_SOURCE_DIR_NAMES = setOf("c_src", "lib", "priv", "src")

// ---------------------------------------------------------------------------
// CoalescedRequests - output of coalesceRequests, input to buildSyncPlan
// ---------------------------------------------------------------------------

internal data class CoalescedRequests(
    val deleteAlls: List<SyncRequest.DeleteAll>,
    val deleteOnes: List<SyncRequest.DeleteOne>,
    val hasAll: Boolean,
    val syncRoots: List<SyncRequest.SyncRoot>,
    val depsRoots: List<SyncRequest.DepsRoot>,
    val depRoots: List<SyncRequest.DepRoot>,
    val syncModuleNames: Set<String>,
)

// ---------------------------------------------------------------------------
// resolvePathShapedRequests - first in drain(); validates under readAction
// ---------------------------------------------------------------------------

/**
 * Validates and resolves listener-produced path-shaped requests under read access.
 *
 * The listener is intentionally limited to string/path-shape checks. This function is the first
 * point where project-model APIs are consulted for candidate content roots or owning modules.
 */
internal suspend fun resolvePathShapedRequests(project: Project, requests: List<SyncRequest>): List<SyncRequest> =
    readAction {
        val contentRootUrls = ProjectRootManager
            .getInstance(project)
            .contentRoots
            .mapTo(HashSet()) { it.url }

        requests.mapNotNull { request -> resolvePathShapedRequest(project, request, contentRootUrls) }
    }

private fun resolvePathShapedRequest(
    project: Project,
    request: SyncRequest,
    contentRootUrls: Set<String>,
): SyncRequest? =
    when (request) {
        is SyncRequest.BuildPath ->
            if (isContentRootCandidate(request.contentRootCandidate, contentRootUrls)) {
                SyncRequest.SyncRoot(request.contentRootCandidate.url)
            } else {
                null
            }

        is SyncRequest.BuildDep -> {
            val contentRoot = request.contentRootCandidate
            if (!isContentRootCandidate(contentRoot, contentRootUrls)) {
                null
            } else {
                contentRoot
                    .findChild("deps")
                    ?.findChild(request.depName)
                    ?.takeIf { it.isValid && it.isDirectory }
                    ?.let { SyncRequest.DepRoot(it) }
            }
        }

        is SyncRequest.DepsRoot ->
            if (request.depsRoot.isValid &&
                request.depsRoot.name == "deps" &&
                request.depsRoot.parent?.url in contentRootUrls
            ) {
                request
            } else {
                null
            }

        is SyncRequest.DepRoot ->
            if (request.depRoot.isValid &&
                request.depRoot.parent?.name == "deps" &&
                request.depRoot.parent?.parent?.url in contentRootUrls
            ) {
                request
            } else {
                null
            }

        is SyncRequest.DeleteAll ->
            if (request.contentRootUrl == null || request.contentRootUrl in contentRootUrls) request else null

        is SyncRequest.DeleteOne ->
            if (request.contentRootUrl == null || request.contentRootUrl in contentRootUrls) request else null

        is SyncRequest.MixFile ->
            resolveMixFileRequest(project, request.mixFile)

        SyncRequest.All,
        is SyncRequest.SyncRoot,
        is SyncRequest.SyncModule ->
            request
    }

private fun isContentRootCandidate(file: VirtualFile, contentRootUrls: Set<String>): Boolean =
    file.isValid && file.url in contentRootUrls

private fun resolveMixFileRequest(project: Project, mixFile: VirtualFile): SyncRequest.SyncModule? {
    if (!mixFile.isValid || mixFile.name != MixProject.MIX_EXS) return null
    val module = ModuleUtil.findModuleForFile(mixFile, project) ?: return null
    val mixRoot = mixFile.parent ?: return null
    val inContentRoot = ModuleRootManager
        .getInstance(module)
        .contentRoots
        .any { contentRoot -> contentRoot == mixRoot }
    return if (inContentRoot) SyncRequest.SyncModule(module) else null
}

// ---------------------------------------------------------------------------
// coalesceRequests
// ---------------------------------------------------------------------------

internal fun coalesceRequests(requests: List<SyncRequest>): CoalescedRequests {
    val deleteAlls = requests.filterIsInstance<SyncRequest.DeleteAll>()
    val deleteOnes = requests.filterIsInstance<SyncRequest.DeleteOne>()
    val hasAll = requests.any { it is SyncRequest.All }
    val deleteAllUrls = deleteAlls.map { it.depsUrl }.toSet()

    // SyncRoot requests are filtered the same way as DepsRoot: suppressed by All and by
    // DeleteAll for the same content root. DeleteAll.depsUrl is always
    // "<contentRootUrl>/deps", so we compare with exact equality rather than a prefix
    // startsWith check. A startsWith check would incorrectly suppress a pending SyncRoot
    // for file:///repo/app when a DeleteAll targets file:///repo/app2/deps, because the
    // sibling root URL is a prefix of the other's depsUrl string.
    val syncRoots = if (hasAll) emptyList() else {
        requests.filterIsInstance<SyncRequest.SyncRoot>()
            .distinctBy { it.contentRootUrl }
            .filter { syncRoot ->
                deleteAllUrls.none { depsUrl -> depsUrl == "${syncRoot.contentRootUrl}/deps" }
            }
    }

    val depsRoots = if (hasAll) emptyList() else {
        requests.filterIsInstance<SyncRequest.DepsRoot>()
            .filter { depsRoot -> deleteAllUrls.none { url -> depsRoot.depsRoot.url == url } }
    }

    val depRoots = if (hasAll) emptyList() else {
        val depsRootUrls = depsRoots.map { it.depsRoot.url }.toSet()
        val syncRootContentUrls = syncRoots.map { it.contentRootUrl }.toSet()
        requests.filterIsInstance<SyncRequest.DepRoot>()
            .filter { depRoot -> depsRootUrls.none { url -> depRoot.depRoot.parent?.url == url } }
            .filter { depRoot -> deleteAllUrls.none { url -> depRoot.depRoot.parent?.url == url } }
            // Also suppress DepRoot when a SyncRoot covers the same content root (the SyncRoot
            // will enumerate all deps under that root anyway).
            .filter { depRoot -> syncRootContentUrls.none { url -> depRoot.depRoot.parent?.parent?.url == url } }
    }

    val syncModuleNames = requests.filterIsInstance<SyncRequest.SyncModule>()
        .mapTo(LinkedHashSet()) { it.moduleName }

    return CoalescedRequests(
        deleteAlls = deleteAlls,
        deleteOnes = deleteOnes,
        hasAll = hasAll,
        syncRoots = syncRoots,
        depsRoots = depsRoots,
        depRoots = depRoots,
        syncModuleNames = syncModuleNames,
    )
}

// ---------------------------------------------------------------------------
// buildSyncPlan - main read-phase orchestrator
// ---------------------------------------------------------------------------

internal suspend fun buildSyncPlan(project: Project, requests: CoalescedRequests): SyncPlan {
    val requestedLibraryPlans = when {
        requests.hasAll -> buildLibraryRootsPlans(project, allDepRoots(project))
        requests.syncRoots.isNotEmpty() || requests.depsRoots.isNotEmpty() || requests.depRoots.isNotEmpty() -> {
            val depRoots = readAction {
                // SyncRoot: enumerate all deps under the specified content root (identified by URL).
                // SyncRoot is only produced from BuildPath after the URL is verified as a registered
                // module content root (see resolvePathShapedRequest), so the firstOrNull lookup
                // always succeeds in production.
                val fromSyncRoots = requests.syncRoots.flatMap { syncRoot ->
                    ProjectRootManager.getInstance(project).contentRoots
                        .firstOrNull { it.url == syncRoot.contentRootUrl }
                        ?.findChild("deps")?.children?.toList()
                        ?: emptyList()
                }
                val fromDepsRoots = requests.depsRoots.flatMap { depsRoot ->
                    if (depsRoot.depsRoot.isValid) depsRoot.depsRoot.children.toList() else emptyList()
                }
                val fromDepRoots = requests.depRoots.mapNotNull { depRoot ->
                    depRoot.depRoot.takeIf { it.isValid }
                }
                fromSyncRoots + fromDepsRoots + fromDepRoots
            }
            buildLibraryRootsPlans(project, depRoots)
        }
        // When only module-scoped requests are present (e.g. from a MixFile content-change
        // event that resolved to a SyncModule), build library plans from those modules' own
        // content roots so that deps-owner scoping works correctly.
        //
        // Without this branch, buildModuleDepsPlan receives an empty requestedLibraryPlans
        // list and an umbrella child module whose deps live under the umbrella root falls
        // through to the mix.exs content-root fallback, producing a mismatched placeholder
        // name like "phoenix [parent/apps/child1]" instead of the correct "phoenix [parent]".
        //
        // The ancestor walk handles umbrella children: when a child module at
        // parent/apps/child1 has no deps/ under its own content root, we walk up the
        // directory tree until we find a mix.exs-bearing ancestor that has a deps/ directory.
        // This ensures the library plans carry the umbrella root's content-root URL.
        requests.syncModuleNames.isNotEmpty() -> {
            val depRoots = readAction {
                val moduleManager = ModuleManager.getInstance(project)
                requests.syncModuleNames.flatMap { moduleName ->
                    moduleManager.findModuleByName(moduleName)
                        ?.takeIf { !it.isDisposed }
                        ?.let { ModuleRootManager.getInstance(it).contentRoots.toList() }
                        .orEmpty()
                }.flatMap { contentRoot ->
                    // First try the module's own deps/ directory.
                    val ownDeps = contentRoot.findChild("deps")?.children?.toList()
                    if (!ownDeps.isNullOrEmpty()) {
                        ownDeps
                    } else {
                        // Walk up ancestors to find the nearest umbrella root with deps/.
                        // Stop when we find a directory containing both mix.exs and deps/.
                        findAncestorDeps(contentRoot)
                    }
                }
            }
            buildLibraryRootsPlans(project, depRoots)
        }
        else -> emptyList()
    }

    val moduleNames = LinkedHashSet(requests.syncModuleNames)
    if (requestedLibraryPlans.isNotEmpty()) {
        // For a full All-sync, fan-out to every module in the project.
        // For scoped (DepRoot / DepsRoot) syncs, only fan-out to modules that own the
        // affected content roots - avoids wiring unrelated modules.
        moduleNames += if (requests.hasAll) {
            allModuleNames(project)
        } else {
            affectedModuleNamesForLibraryPlans(project, requestedLibraryPlans)
        }
    }

    // Pass requestedLibraryPlans so that buildModuleDepsPlan can resolve the correct
    // scoped library name for umbrella child modules whose dep directory is not under
    // their own content root but IS the root of an already-computed library plan
    // (e.g. "phoenix [parent]" for a child at "parent/apps/child1").
    val modulePlans = buildModuleDepsPlans(project, moduleNames, requestedLibraryPlans)
    val externalLibraryPlans = modulePlans.flatMap { it.externalLibraryPlans }
    val libraryPlans = deduplicateLibraryPlans(requestedLibraryPlans + externalLibraryPlans)

    return SyncPlan(
        deleteAlls = requests.deleteAlls.map { DeleteAllPlan(it.depsUrl) },
        deleteOnes = requests.deleteOnes.map { DeleteOnePlan(it.depName, it.contentRootUrl) },
        libraryPlans = libraryPlans,
        modulePlans = modulePlans,
    )
}

private suspend fun allDepRoots(project: Project): List<VirtualFile> =
    readAction {
        ProjectRootManager
            .getInstance(project)
            .contentRoots
            .flatMap { contentRoot -> contentRoot.findChild("deps")?.children?.toList() ?: emptyList() }
    }

/**
 * Walks up the directory tree from [contentRoot] to find the nearest ancestor that contains
 * both a `mix.exs` file and a `deps/` directory.  Returns the children of that ancestor's
 * `deps/` directory.
 *
 * This handles umbrella child modules at `parent/apps/child1` whose deps physically live
 * under `parent/deps/`.  The walk stops when:
 * - An ancestor with both `mix.exs` and `deps/` is found (the umbrella root).
 * - The directory tree is exhausted (parent is null).
 * - More than [MAX_ANCESTOR_WALK_DEPTH] levels are traversed (safety bound).
 *
 * **Must be called inside [readAction].**
 */
private fun findAncestorDeps(contentRoot: VirtualFile): List<VirtualFile> {
    var current = contentRoot.parent
    var depth = 0
    while (current != null && depth < MAX_ANCESTOR_WALK_DEPTH) {
        val hasMixExs = current.findChild("mix.exs")?.isValid == true
        val deps = current.findChild("deps")
        if (hasMixExs && deps != null && deps.isDirectory) {
            return deps.children?.toList().orEmpty()
        }
        current = current.parent
        depth++
    }
    return emptyList()
}

private suspend fun allModuleNames(project: Project): List<String> =
    readAction {
        ModuleManager
            .getInstance(project)
            .modules
            .filter { !it.isDisposed }
            .map { it.name }
    }

/**
 * Returns the names of modules whose content roots overlap with the content roots referenced
 * by [libraryPlans].
 *
 * Used for scoped (non-All) syncs so that a single-root [SyncRequest.DepRoot] does not
 * trigger full module-plan computation for unrelated modules.
 *
 * **Matching rules:**
 * 1. Exact match - the module's content root URL equals the library plan's content root URL.
 * 2. Ancestor match - the module's content root URL is a descendant of the library plan's
 *    content root URL (separated by a "/"). This covers umbrella child modules at
 *    `parent/apps/child1` when a library plan targets the umbrella root `parent/`; without
 *    this rule, a scoped DepRoot event for `parent/deps/phoenix` would never plan child
 *    modules and they would remain un-wired after a deps sync.
 *
 * **Note:** this function iterates [ModuleManager.modules] to identify the affected subset.
 * That lightweight filter pass does NOT perform module-plan computation for every module -
 * only modules whose content roots overlap with [libraryPlans] proceed to full plan
 * computation. Modules outside the affected set are therefore never touched by a scoped sync,
 * even though all module names are read here for filtering purposes.
 */
private suspend fun affectedModuleNamesForLibraryPlans(
    project: Project,
    libraryPlans: List<LibraryRootsPlan>,
): Set<String> =
    readAction {
        val affectedContentRootUrls = libraryPlans.mapTo(HashSet()) { it.contentRootUrl }
        ModuleManager.getInstance(project)
            .modules
            .filter { !it.isDisposed }
            .filter { module ->
                ModuleRootManager.getInstance(module).contentRoots.any { cr ->
                    // Exact: the module's content root IS the deps-owner root.
                    cr.url in affectedContentRootUrls ||
                    // Ancestor: the module's content root is a child of a deps-owner root
                    // (e.g. parent/apps/child1 is under parent/).
                    // VfsUtilCore.isEqualOrAncestor requires the ancestor arg to end with "/"
                    // to be path-boundary-safe; it is always satisfied because we normalise
                    // with the trailing "/" here.
                    affectedContentRootUrls.any { ancestorUrl ->
                        val normalizedAncestor = if (ancestorUrl.endsWith("/")) ancestorUrl else "$ancestorUrl/"
                        VfsUtilCore.isEqualOrAncestor(normalizedAncestor, cr.url)
                    }
                }
            }
            .mapTo(LinkedHashSet()) { it.name }
    }

// ---------------------------------------------------------------------------
// Module dep plan building
// ---------------------------------------------------------------------------

private suspend fun buildModuleDepsPlans(
    project: Project,
    moduleNames: Set<String>,
    requestedLibraryPlans: List<LibraryRootsPlan>,
): List<ModuleDepsPlan> {
    if (moduleNames.isEmpty()) return emptyList()

    return moduleNames.mapNotNull { moduleName ->
        currentCoroutineContext().ensureActive()
        ProgressManager.checkCanceled()
        buildModuleDepsPlan(project, moduleName, requestedLibraryPlans)
    }
}

/**
 * Builds the dependency plan for one module.
 *
 * [requestedLibraryPlans] is used as a tertiary fallback when resolving the scoped library
 * name for a dep whose physical directory is not under the module's own content roots and not
 * reachable via the external-path path.  This handles umbrella child modules whose deps are
 * owned by the umbrella root: the library plan for "parent/deps/phoenix" has
 * [LibraryRootsPlan.contentRootUrl] = "parent", so both child modules reference the same
 * `"phoenix \[parent\]"` library rather than each creating a placeholder scoped to their own root.
 */
internal suspend fun buildModuleDepsPlan(
    project: Project,
    moduleName: String,
    requestedLibraryPlans: List<LibraryRootsPlan>,
): ModuleDepsPlan? {
    val contentRoots = readAction {
        ModuleManager
            .getInstance(project)
            .findModuleByName(moduleName)
            ?.takeIf { !it.isDisposed }
            ?.let { ModuleRootManager.getInstance(it).contentRoots }
            ?: return@readAction null
    } ?: return null

    val psiManager = PsiManager.getInstance(project)
    val indicator = EmptyProgressIndicator()
    val deps = transitiveResolution(project, psiManager, indicator, *contentRoots).toList()
    if (deps.isEmpty()) return null

    val externalLibraryPlans = buildExternalLibraryPlans(project, deps)

    // Compute scoped library names for the module's library deps.
    // Resolution is restricted to this module's own content roots (not global) to prevent
    // cross-module contamination when multiple modules declare the same dep name.
    // dep.path is "deps/<name>" (relative). We search only this module's content roots so
    // that module B never resolves against module A's deps/phoenix directory.
    // If the dep directory is not yet fetched, fall back in order:
    //   1. the name from the already-computed external library plan for this dep - when the
    //      dep's `path:` option points outside all content roots (e.g. an absolute path or a
    //      relative `"../sibling"` path) the dep directory is not findable via
    //      `findFileByRelativePath`. Without this check the module order entry would be
    //      scoped to the mix.exs content root instead of the external path, producing a name
    //      that does not match the library created by `applyLibraryPlans` for the external dep.
    //   2. the library name from the already-computed requestedLibraryPlans for the same dep
    //      name - handles umbrella child modules where the dep physically lives under the
    //      umbrella root's deps/ directory, which is NOT a content root of the child module.
    //      Uses the plan whose contentRootUrl is an ancestor of one of this module's content
    //      roots, choosing the nearest (longest URL = most specific) match. This prevents a
    //      child module under umbrella_b from being wired to umbrella_a's scoped library when
    //      both umbrellas contain a dep with the same name (e.g. both have deps/phoenix).
    //   3. the first content root that has a mix.exs (the actual Mix project root)
    //   4. the first content root overall
    // This ensures every declared dep stays in libraryDeps even before `mix deps.get` has
    // run, so that the missingLibraryDeps path creates a placeholder library for it.
    val externalLibNames: Map<String, String> = externalLibraryPlans.associateBy({ it.depName }, { it.libraryName })
    val libraryDeps: Set<String> = readAction {
        val mixExsRoots by lazy { contentRoots.filter { it.findFileByRelativePath("mix.exs") != null } }
        deps.filter { it.type == Dep.Type.LIBRARY }.mapTo(LinkedHashSet()) { dep ->
            val owningRoot = contentRoots.firstOrNull { root ->
                root.findFileByRelativePath(dep.path)?.isValid == true
            }
            if (owningRoot != null) {
                scopedDepLibraryName(owningRoot.url, dep.application)
            } else {
                externalLibNames[dep.application]
                    ?: requestedLibraryPlans
                        .filter { it.depName == dep.application }
                        .filter { plan ->
                            // Only consider library plans whose content root is an ancestor of
                            // one of this module's own content roots. This prevents a child
                            // module under umbrella_b from being wired to umbrella_a's plan
                            // when both umbrellas declare the same dep name. Without the "/"
                            // suffix, "file:///parent/app" would match "file:///parent/app2".
                            val normalizedPlanRoot = if (plan.contentRootUrl.endsWith("/"))
                                plan.contentRootUrl
                            else
                                "${plan.contentRootUrl}/"
                            contentRoots.any { cr ->
                                VfsUtilCore.isEqualOrAncestor(normalizedPlanRoot, cr.url)
                            }
                        }
                        // Prefer the nearest ancestor (longest contentRootUrl = most specific).
                        // "file:///parent/apps/child" is more specific than "file:///parent"
                        // should one ever be a registered content root and a deps owner.
                        .maxByOrNull { it.contentRootUrl.length }
                        ?.libraryName
                    ?: scopedDepLibraryName(
                        mixExsRoots.firstOrNull()?.url ?: contentRoots.firstOrNull()?.url ?: "",
                        dep.application
                    )
            }
        }
    }

    return ModuleDepsPlan(
        moduleName = moduleName,
        moduleDeps = deps.filter { it.type == Dep.Type.MODULE }.mapTo(LinkedHashSet()) { it.application },
        libraryDeps = libraryDeps,
        externalLibraryPlans = externalLibraryPlans,
    )
}

/** Syncs libraries for dep paths that live outside this project's content roots. */
private suspend fun buildExternalLibraryPlans(project: Project, deps: Collection<Dep>): List<LibraryRootsPlan> {
    val externalPaths = readAction {
        val projectRootManager = ProjectRootManager.getInstance(project)
        val projectFileIndex = projectRootManager.fileIndex
        deps.mapNotNull { dep ->
            ProgressManager.checkCanceled()
            dep.virtualFile(project)?.let { virtualFile ->
                if (projectFileIndex.getContentRootForFile(virtualFile) == null &&
                    !projectFileIndex.isInLibrary(virtualFile) &&
                    !projectFileIndex.isExcluded(virtualFile)
                ) {
                    virtualFile
                } else {
                    null
                }
            }
        }
    }

    return buildLibraryRootsPlans(project, externalPaths)
}

// ---------------------------------------------------------------------------
// Library roots plan building
// ---------------------------------------------------------------------------

private suspend fun buildLibraryRootsPlans(project: Project, deps: Collection<VirtualFile>): List<LibraryRootsPlan> =
    readAction {
        buildLibraryRootsPlansInCurrentContext(project, deps)
    }

@RequiresReadLock
internal fun buildLibraryRootsPlansInCurrentContext(project: Project, deps: Collection<VirtualFile>): List<LibraryRootsPlan> {
    ThreadingAssertions.assertReadAccess()
    if (deps.isEmpty()) return emptyList()

    // Fall-back _build roots for external deps that don't sit under a project content root.
    val allBuildRoots by lazy {
        ProjectRootManager
            .getInstance(project)
            .contentRoots
            .mapNotNull { it.findChild("_build") }
    }

    return deps
        .filter { it.isValid && it.isDirectory }
        .map { dep ->
            ProgressManager.checkCanceled()
            val depName = dep.name
            // The content root is the grandparent of the dep: <contentRoot>/deps/<depName>.
            // Scope the _build scan to this dep's own content root to prevent cross-contamination
            // between content roots that share a dep name.
            val contentRoot = dep.parent?.parent
            val contentRootUrl = contentRoot?.url ?: ""
            val buildRootsToScan = if (contentRoot != null)
                listOfNotNull(contentRoot.findChild("_build"))
            else
                allBuildRoots

            val classRoots = ArrayList<VirtualFile>()
            val excludeFolders = ArrayList<ExcludeFolderPlan>()

            for (build in buildRootsToScan) {
                ProgressManager.checkCanceled()
                for (environment in build.children.filter { it.isDirectory }) {
                    ProgressManager.checkCanceled()
                    for (environmentChild in environment.children.filter { it.isDirectory }) {
                        ProgressManager.checkCanceled()
                        when (environmentChild.name) {
                            "consolidated" -> classRoots += environmentChild
                            "lib" -> environmentChild.findChild(depName)?.let { depEnvLib ->
                                depEnvLib.findChild("ebin")?.let { ebin ->
                                    if (ebin.isDirectory) {
                                        ModuleUtil.findModuleForFile(depEnvLib, project)?.let { module ->
                                            excludeFolders += ExcludeFolderPlan(module.name, depEnvLib.url)
                                        }
                                        classRoots += ebin
                                    }
                                }
                            }
                        }
                    }
                }
            }

            LibraryRootsPlan(
                contentRootUrl = contentRootUrl,
                depName = depName,
                classRootUrls = classRoots.map { it.url }.distinct(),
                sourceRootUrls = dep.children
                    .filter { child -> child.isDirectory && child.name in MIX_DEP_SOURCE_DIR_NAMES }
                    .map { child -> child.url }
                    .distinct(),
                excludeFolders = excludeFolders.distinctBy { it.moduleName to it.folderUrl },
            )
        }
}

internal fun deduplicateLibraryPlans(plans: List<LibraryRootsPlan>): List<LibraryRootsPlan> =
    plans.associateBy { it.libraryName }.values.toList()
