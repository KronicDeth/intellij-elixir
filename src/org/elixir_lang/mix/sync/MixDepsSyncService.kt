package org.elixir_lang.mix.sync

import com.google.common.annotations.VisibleForTesting
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.psi.PsiManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.elixir_lang.mix.Dep
import org.elixir_lang.mix.Project as MixProject
import org.elixir_lang.mix.library.Kind
import org.elixir_lang.mix.watcher.TransitiveResolution.transitiveResolution
import java.net.URI
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.milliseconds

/**
 * Project-level service that owns the lifecycle of the Mix dep-sync pipeline.
 *
 * All [SyncRequest]s - including deletes and per-module mix.exs re-syncs - flow through a single
 * serialized [MutableSharedFlow] / [debounce] / [Mutex] pipeline: deletes drain before syncs,
 * and only one sync operation runs at a time.
 *
 * Lifecycle:
 * - The service scope is injected by the platform and cancelled automatically on project close or plugin unload.
 * - Listeners call [enqueue] from VFS callback threads; this is always lightweight (classify + tryEmit).
 *
 * Coalescing rules (enforced in [drain]):
 * - A pending [SyncRequest.All] supersedes all pending [SyncRequest.DepsRoot] and [SyncRequest.DepRoot] requests.
 * - A pending [SyncRequest.DeleteAll] supersedes any pending [SyncRequest.DepsRoot] / [SyncRequest.DepRoot] for the same tree.
 * - Deletes are executed before syncs so that a delete followed immediately by a re-sync does not
 *   re-populate libraries that were intentionally removed.
 *
 * Write mutations use [edtWriteAction]: all model mutations dispatch to EDT + write-lock, which
 * avoids `invokeAndWait`-based deadlocks from coroutine context.
 *
 * Observability:
 * - [LOG] emits `debug`-level messages at drain-start and drain-complete with request counts and
 *   elapsed time, providing a ready hook for structured metrics without model changes.
 */
@Service(Service.Level.PROJECT)
class MixDepsSyncService(private val project: Project, cs: CoroutineScope) {
    private data class CoalescedRequests(
        val deleteAlls: List<SyncRequest.DeleteAll>,
        val deleteOnes: List<SyncRequest.DeleteOne>,
        val hasAll: Boolean,
        val depsRoots: List<SyncRequest.DepsRoot>,
        val depRoots: List<SyncRequest.DepRoot>,
        val syncModuleNames: Set<String>,
    )

    private data class DeleteAllPlan(val depsUrl: String)

    private data class DeleteOnePlan(val depName: String)

    private data class ExcludeFolderPlan(
        val moduleName: String,
        val folderUrl: String,
    )

    private data class LibraryRootsPlan(
        val depName: String,
        val classRootUrls: List<String>,
        val sourceRootUrls: List<String>,
        val excludeFolders: List<ExcludeFolderPlan>,
    )

    private data class ModuleDepsPlan(
        val moduleName: String,
        val moduleDeps: Set<String>,
        val libraryDeps: Set<String>,
        val externalLibraryPlans: List<LibraryRootsPlan>,
    )

    private data class SyncPlan(
        val deleteAlls: List<DeleteAllPlan> = emptyList(),
        val deleteOnes: List<DeleteOnePlan> = emptyList(),
        val libraryPlans: List<LibraryRootsPlan> = emptyList(),
        val modulePlans: List<ModuleDepsPlan> = emptyList(),
    ) {
        val isEmpty: Boolean
            get() = deleteAlls.isEmpty() &&
                deleteOnes.isEmpty() &&
                libraryPlans.isEmpty() &&
                modulePlans.isEmpty()
    }

    // ------------------------------------------------------------------
    // Pending request accumulator (thread-safe, filled from VFS callbacks)
    // AtomicReference<Set> allows drain() to snapshot-and-clear atomically via getAndSet(),
    // eliminating the race between reading and clearing that exists with a plain mutable set.
    // ------------------------------------------------------------------
    private val pendingRequests: AtomicReference<Set<SyncRequest>> = AtomicReference(emptySet())

    // ------------------------------------------------------------------
    // Flow + debounce
    // replay = 1 ensures the most-recent trigger emission survives until the collector subscribes,
    // protecting against the startup-timing gap where cs.launch has not yet reached collect().
    // ------------------------------------------------------------------
    private val syncFlow = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // ------------------------------------------------------------------
    // Serialisation: only one drain executes at a time.
    // ------------------------------------------------------------------
    private val syncMutex = Mutex()

    init {
        @OptIn(FlowPreview::class)
        cs.launch {
            syncFlow
                .debounce(DEBOUNCE_MS.milliseconds)
                .collect {
                    // supervisorScope isolates the collector from child failures: if the
                    // async drain child is cancelled (user presses Cancel on the progress
                    // indicator) or throws, the SupervisorJob does NOT propagate that
                    // failure to the collector - only the child is affected.
                    supervisorScope {
                        val drainAttempt = async { drain() }

                        try {
                            drainAttempt.await()
                        } catch (
                            @Suppress("IncorrectCancellationExceptionHandling")
                            _: CancellationException
                        ) {
                            // CancellationException here means either:
                            //   (a) The user cancelled a progress task - only the drain
                            //       child was cancelled; the collector must survive to
                            //       process future VFS events.
                            //   (b) The service scope is being torn down (project close) -
                            //       the collector should terminate.
                            //
                            // ensureActive() distinguishes the two: it rethrows if the
                            // collector's own Job is cancelled (case b), and is a no-op if
                            // the collector is still active (case a). We deliberately do NOT
                            // rethrow in case (a) - doing so would kill the collector and
                            // permanently disable dep watching for the rest of the project
                            // lifetime.
                            currentCoroutineContext().ensureActive()
                            LOG.debug("MixDepsSyncService: drain cancelled, will retry on next trigger")
                        } catch (e: Throwable) {
                            LOG.error("MixDepsSyncService: drain failed, will retry on next trigger", e)
                        }
                    }
                }
        }
    }

    // ------------------------------------------------------------------
    // Public API for thin listener adapters (called from VFS callbacks)
    // ------------------------------------------------------------------

    /**
     * Enqueues a sync request and triggers the debounce timer.
     *
     * This method is designed to be called from VFS listener callbacks. It is always lightweight:
     * it atomically updates the pending set and calls [MutableSharedFlow.tryEmit], which never suspends.
     */
    fun enqueue(request: SyncRequest) {
        // getAndUpdate is an atomic read-modify-write, so no request can be lost between
        // drain()'s getAndSet(emptySet()) and a concurrent enqueue() call.
        pendingRequests.getAndUpdate { it + request }
        syncFlow.tryEmit(Unit)
    }

    /** Number of requests currently waiting in the pending set. Exposed for tests only. */
    @VisibleForTesting
    internal val pendingCount: Int get() = pendingRequests.get().size

    /** Clears the pending-request accumulator. Must only be called from tests to reset state between test methods. */
    @VisibleForTesting
    internal fun clearPendingForTesting() {
        pendingRequests.set(emptySet())
    }

    // ------------------------------------------------------------------
    // Drain (runs inside coroutine, serialised by mutex)
    // ------------------------------------------------------------------
    @VisibleForTesting
    internal suspend fun drain() {
        syncMutex.withLock {
            if (project.isDisposed) return

            // getAndSet atomically replaces the pending set with an empty set and returns the
            // previous contents. Any enqueue() calls racing with this line will either land in the
            // old (now-returned) set (if they completed before getAndSet) or in the new empty set
            // (if they run after), where they will be picked up by the next drain triggered by
            // their own tryEmit(). No request can be silently dropped.
            val rawRequests: List<SyncRequest> = pendingRequests.getAndSet(emptySet()).toList()
            if (rawRequests.isEmpty()) return

            val requests = resolvePathShapedRequests(rawRequests)
            if (requests.isEmpty()) return

            val drainStartNs = System.nanoTime()
            LOG.debug("MixDepsSyncService: draining ${requests.size} request(s)")

            val coalescedRequests = coalesceRequests(requests)
            val syncPlan = withBackgroundProgress(project, "Syncing Elixir dependencies") {
                withContext(Dispatchers.Default) {
                    buildSyncPlan(coalescedRequests)
                }
            }

            if (!syncPlan.isEmpty) {
                withBackgroundProgress(project, "Applying Elixir dependency sync") {
                    applySyncPlan(syncPlan)
                }
            }

            val elapsedMs = (System.nanoTime() - drainStartNs) / 1_000_000
            LOG.debug(
                "MixDepsSyncService: drain complete - ${requests.size} request(s) in ${elapsedMs}ms " +
                    "(deleteAlls=${coalescedRequests.deleteAlls.size}, " +
                    "deleteOnes=${coalescedRequests.deleteOnes.size}, " +
                    "hasAll=${coalescedRequests.hasAll}, " +
                    "depsRoots=${coalescedRequests.depsRoots.size}, " +
                    "depRoots=${coalescedRequests.depRoots.size}, " +
                    "syncModules=${coalescedRequests.syncModuleNames.size}, " +
                    "libraryPlans=${syncPlan.libraryPlans.size}, " +
                    "modulePlans=${syncPlan.modulePlans.size})"
            )
        }
    }

    private fun coalesceRequests(requests: List<SyncRequest>): CoalescedRequests {
        val deleteAlls = requests.filterIsInstance<SyncRequest.DeleteAll>()
        val deleteOnes = requests.filterIsInstance<SyncRequest.DeleteOne>()
        val hasAll = requests.any { it is SyncRequest.All }
        val deleteAllUrls = deleteAlls.map { it.depsUrl }.toSet()

        val depsRoots = if (hasAll) emptyList() else {
            requests.filterIsInstance<SyncRequest.DepsRoot>()
                .filter { depsRoot -> deleteAllUrls.none { url -> depsRoot.depsRoot.url == url } }
        }

        val depRoots = if (hasAll) emptyList() else {
            val depsRootUrls = depsRoots.map { it.depsRoot.url }.toSet()
            requests.filterIsInstance<SyncRequest.DepRoot>()
                .filter { depRoot -> depsRootUrls.none { url -> depRoot.depRoot.parent?.url == url } }
                .filter { depRoot -> deleteAllUrls.none { url -> depRoot.depRoot.parent?.url == url } }
        }

        val syncModuleNames = requests.filterIsInstance<SyncRequest.SyncModule>()
            .mapTo(LinkedHashSet()) { it.moduleName }

        return CoalescedRequests(
            deleteAlls = deleteAlls,
            deleteOnes = deleteOnes,
            hasAll = hasAll,
            depsRoots = depsRoots,
            depRoots = depRoots,
            syncModuleNames = syncModuleNames,
        )
    }

    private suspend fun buildSyncPlan(requests: CoalescedRequests): SyncPlan {
        val requestedLibraryPlans = when {
            requests.hasAll -> buildLibraryRootsPlans(allDepRoots())
            requests.depsRoots.isNotEmpty() || requests.depRoots.isNotEmpty() -> {
                val depRoots = readAction {
                    requests.depsRoots.flatMap { depsRoot ->
                        if (depsRoot.depsRoot.isValid) depsRoot.depsRoot.children.toList() else emptyList()
                    } + requests.depRoots.mapNotNull { depRoot ->
                        depRoot.depRoot.takeIf { it.isValid }
                    }
                }
                buildLibraryRootsPlans(depRoots)
            }
            else -> emptyList()
        }

        val moduleNames = LinkedHashSet(requests.syncModuleNames)
        if (requestedLibraryPlans.isNotEmpty()) {
            moduleNames += allModuleNames()
        }

        val modulePlans = buildModuleDepsPlans(moduleNames)
        val externalLibraryPlans = modulePlans.flatMap { it.externalLibraryPlans }
        val libraryPlans = deduplicateLibraryPlans(requestedLibraryPlans + externalLibraryPlans)

        return SyncPlan(
            deleteAlls = requests.deleteAlls.map { DeleteAllPlan(it.depsUrl) },
            deleteOnes = requests.deleteOnes.map { DeleteOnePlan(it.depName) },
            libraryPlans = libraryPlans,
            modulePlans = modulePlans,
        )
    }

    private suspend fun allDepRoots(): List<VirtualFile> =
        readAction {
            ProjectRootManager
                .getInstance(project)
                .contentRootsFromAllModules
                .flatMap { contentRoot -> contentRoot.findChild("deps")?.children?.toList() ?: emptyList() }
        }

    private suspend fun allModuleNames(): List<String> =
        readAction {
            ModuleManager
                .getInstance(project)
                .modules
                .filter { !it.isDisposed }
                .map { it.name }
        }

    private suspend fun buildModuleDepsPlans(moduleNames: Set<String>): List<ModuleDepsPlan> {
        if (moduleNames.isEmpty()) return emptyList()

        return moduleNames.mapNotNull { moduleName ->
            ProgressManager.checkCanceled()
            buildModuleDepsPlan(moduleName)
        }
    }

    private suspend fun buildModuleDepsPlan(moduleName: String): ModuleDepsPlan? {
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

        val externalLibraryPlans = buildExternalLibraryPlans(deps)

        return ModuleDepsPlan(
            moduleName = moduleName,
            moduleDeps = deps.filter { it.type == Dep.Type.MODULE }.mapTo(LinkedHashSet()) { it.application },
            libraryDeps = deps.filter { it.type == Dep.Type.LIBRARY }.mapTo(LinkedHashSet()) { it.application },
            externalLibraryPlans = externalLibraryPlans,
        )
    }

    /** Syncs libraries for dep paths that live outside this project's content roots. */
    private suspend fun buildExternalLibraryPlans(deps: Collection<Dep>): List<LibraryRootsPlan> {
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

        return buildLibraryRootsPlans(externalPaths)
    }

    private suspend fun buildLibraryRootsPlans(deps: Collection<VirtualFile>): List<LibraryRootsPlan> =
        readAction {
            buildLibraryRootsPlansInCurrentContext(deps)
        }

    private fun buildLibraryRootsPlansInCurrentContext(deps: Collection<VirtualFile>): List<LibraryRootsPlan> {
        if (deps.isEmpty()) return emptyList()

        val buildRoots = ProjectRootManager
            .getInstance(project)
            .contentRootsFromAllModules
            .mapNotNull { it.findChild("_build") }

        return deps
            .filter { it.isValid && it.isDirectory }
            .map { dep ->
                ProgressManager.checkCanceled()
                val depName = dep.name
                val classRoots = ArrayList<VirtualFile>()
                val excludeFolders = ArrayList<ExcludeFolderPlan>()

                for (build in buildRoots) {
                    ProgressManager.checkCanceled()
                    for (environment in build.children.filter { it.isDirectory }) {
                        ProgressManager.checkCanceled()
                        for (environmentChild in environment.children.filter { it.isDirectory }) {
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
                    depName = depName,
                    classRootUrls = classRoots.map { it.url }.distinct(),
                    sourceRootUrls = dep.children
                        .filter { child -> child.isDirectory && child.name in SOURCE_NAMES }
                        .map { child -> child.url }
                        .distinct(),
                    excludeFolders = excludeFolders.distinctBy { it.moduleName to it.folderUrl },
                )
            }
    }

    private fun deduplicateLibraryPlans(plans: List<LibraryRootsPlan>): List<LibraryRootsPlan> =
        plans.associateBy { it.depName }.values.toList()

    private suspend fun applySyncPlan(plan: SyncPlan) {
        edtWriteAction {
            if (project.isDisposed) return@edtWriteAction

            for (deleteAll in plan.deleteAlls) {
                if (project.isDisposed) break
                deleteAllLibraries(deleteAll.depsUrl)
            }
            for (deleteOne in plan.deleteOnes) {
                if (project.isDisposed) break
                deleteLibrary(deleteOne.depName)
            }

            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val plannedLibraryNames = plan.libraryPlans.mapTo(HashSet()) { it.depName }
            val missingLibraryDeps = plan.modulePlans
                .flatMap { it.libraryDeps }
                .filterNot { libraryName ->
                    libraryName in plannedLibraryNames || libraryTable.getLibraryByName(libraryName) != null
                }
                .toSet()

            applyLibraryPlans(plan.libraryPlans, missingLibraryDeps, libraryTable)
            applyModulePlans(plan.modulePlans, plan.libraryPlans.flatMap { it.excludeFolders }, libraryTable)
        }
    }

    /**
     * Validates and resolves listener-produced path-shaped requests under read access.
     *
     * The listener is intentionally limited to string/path-shape checks. This method is the first
     * point where project-model APIs are consulted for candidate content roots or owning modules.
     */
    private suspend fun resolvePathShapedRequests(requests: List<SyncRequest>): List<SyncRequest> =
        readAction {
            val contentRootUrls = ProjectRootManager
                .getInstance(project)
                .contentRootsFromAllModules
                .mapTo(HashSet()) { it.url }

            requests.mapNotNull { request -> resolvePathShapedRequest(request, contentRootUrls) }
        }

    private fun resolvePathShapedRequest(
        request: SyncRequest,
        contentRootUrls: Set<String>,
    ): SyncRequest? =
        when (request) {
            is SyncRequest.BuildPath ->
                if (isContentRootCandidate(request.contentRootCandidate, contentRootUrls)) {
                    SyncRequest.All
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
                resolveMixFileRequest(request.mixFile)

            SyncRequest.All,
            is SyncRequest.SyncModule ->
                request
        }

    private fun isContentRootCandidate(file: VirtualFile, contentRootUrls: Set<String>): Boolean =
        file.isValid && file.url in contentRootUrls

    private fun resolveMixFileRequest(mixFile: VirtualFile): SyncRequest.SyncModule? {
        if (!mixFile.isValid || mixFile.name != MixProject.MIX_EXS) return null
        val module = ModuleUtil.findModuleForFile(mixFile, project) ?: return null
        val mixRoot = mixFile.parent ?: return null
        val inContentRoot = ModuleRootManager
            .getInstance(module)
            .contentRoots
            .any { contentRoot -> contentRoot == mixRoot }
        return if (inContentRoot) SyncRequest.SyncModule(module) else null
    }

    // ------------------------------------------------------------------
    // Per-module mix.exs resolution + dependency wiring
    // ------------------------------------------------------------------

    @VisibleForTesting
    internal suspend fun syncLibrariesForModule(module: Module) {
        val modulePlan = buildModuleDepsPlan(module.name) ?: return
        val syncPlan = SyncPlan(
            libraryPlans = deduplicateLibraryPlans(modulePlan.externalLibraryPlans),
            modulePlans = listOf(modulePlan),
        )
        if (!syncPlan.isEmpty) {
            applySyncPlan(syncPlan)
        }
    }

    // ------------------------------------------------------------------
    // Library-table sync (per-dep class/source root population)
    // ------------------------------------------------------------------

    @VisibleForTesting
    internal fun syncLibraries(deps: Array<VirtualFile>, libraryTable: LibraryTable) {
        val libraryPlans = buildLibraryRootsPlansInCurrentContext(deps.toList())
        applyLibraryPlans(libraryPlans, emptySet(), libraryTable)
        applyModulePlans(emptyList(), libraryPlans.flatMap { it.excludeFolders }, libraryTable)
    }

    private fun applyLibraryPlans(
        libraryPlans: List<LibraryRootsPlan>,
        emptyLibraryNames: Set<String>,
        libraryTable: LibraryTable,
    ) {
        if (libraryPlans.isEmpty() && emptyLibraryNames.isEmpty()) return

        val libraryTableModifiableModel = libraryTable.modifiableModel
        var committed = false
        try {
            for (plan in libraryPlans) {
                if (project.isDisposed) break
                val library = libraryTableModifiableModel.getLibraryByName(plan.depName)
                    ?: libraryTableModifiableModel.createLibrary(plan.depName, Kind)
                applyLibraryRootsPlan(library, plan)
            }

            for (libraryName in emptyLibraryNames) {
                if (project.isDisposed) break
                if (libraryTableModifiableModel.getLibraryByName(libraryName) == null) {
                    libraryTableModifiableModel.createLibrary(libraryName, Kind)
                }
            }

            if (libraryTableModifiableModel.isChanged) {
                libraryTableModifiableModel.commit()
                committed = true
            }
        } finally {
            if (!committed) {
                libraryTableModifiableModel.dispose()
            }
        }
    }

    private fun applyLibraryRootsPlan(library: Library, plan: LibraryRootsPlan) {
        val libraryModifiableModel = library.modifiableModel
        var committed = false
        try {
            libraryModifiableModel.syncRoots(OrderRootType.CLASSES, plan.classRootUrls)
            libraryModifiableModel.syncRoots(OrderRootType.SOURCES, plan.sourceRootUrls)

            if (libraryModifiableModel.isChanged) {
                libraryModifiableModel.commit()
                committed = true
            }
        } finally {
            if (!committed) {
                libraryModifiableModel.dispose()
            }
        }
    }

    private fun applyModulePlans(
        modulePlans: List<ModuleDepsPlan>,
        excludeFolderPlans: List<ExcludeFolderPlan>,
        libraryTable: LibraryTable,
    ) {
        val moduleNames = (modulePlans.map { it.moduleName } + excludeFolderPlans.map { it.moduleName })
            .toCollection(LinkedHashSet())
        if (moduleNames.isEmpty()) return

        val moduleManager = ModuleManager.getInstance(project)
        val modulePlansByName = modulePlans.associateBy { it.moduleName }
        val excludeFoldersByModule = excludeFolderPlans.groupBy { it.moduleName }

        for (moduleName in moduleNames) {
            if (project.isDisposed) break
            val module = moduleManager.findModuleByName(moduleName)?.takeIf { !it.isDisposed } ?: continue
            val modifiableModel = ModuleRootManager.getInstance(module).modifiableModel
            var committed = false
            try {
                applyExcludeFolderPlans(modifiableModel, excludeFoldersByModule[moduleName].orEmpty())
                modulePlansByName[moduleName]?.let { modulePlan ->
                    applyModuleDepsPlan(modifiableModel, modulePlan, moduleManager, libraryTable)
                }

                if (modifiableModel.isChanged) {
                    modifiableModel.commit()
                    committed = true
                }
            } finally {
                if (!committed) {
                    modifiableModel.dispose()
                }
            }
        }
    }

    private fun applyExcludeFolderPlans(
        modifiableModel: ModifiableRootModel,
        excludeFolderPlans: List<ExcludeFolderPlan>,
    ) {
        for (excludeFolderPlan in excludeFolderPlans) {
            ProgressManager.checkCanceled()
            val folderUrl = excludeFolderPlan.folderUrl
            for (contentEntry in modifiableModel.contentEntries) {
                ProgressManager.checkCanceled()
                if (VfsUtilCore.isEqualOrAncestor(contentEntry.url, folderUrl) &&
                    folderUrl !in contentEntry.excludeFolderUrls
                ) {
                    contentEntry.addExcludeFolder(folderUrl)
                }
            }
        }
    }

    private fun applyModuleDepsPlan(
        modifiableModel: ModifiableRootModel,
        modulePlan: ModuleDepsPlan,
        moduleManager: ModuleManager,
        libraryTable: LibraryTable,
    ) {
        for (depName in modulePlan.moduleDeps) {
            val depModule = moduleManager.findModuleByName(depName)
            when {
                depModule != null && modifiableModel.findModuleOrderEntry(depModule) == null ->
                    modifiableModel.addModuleOrderEntry(depModule)
                depModule == null && modifiableModel.orderEntries.none {
                    it is ModuleOrderEntry && it.moduleName == depName
                } ->
                    modifiableModel.addInvalidModuleEntry(depName)
            }
        }

        for (depName in modulePlan.libraryDeps) {
            val depLibrary = libraryTable.getLibraryByName(depName)
            when {
                depLibrary != null && modifiableModel.findLibraryOrderEntry(depLibrary) == null ->
                    modifiableModel.addLibraryEntry(depLibrary)
                depLibrary == null && modifiableModel.orderEntries.none {
                    it is LibraryOrderEntry && it.libraryName == depName
                } ->
                    modifiableModel.addInvalidLibrary(depName, LibraryTablesRegistrar.PROJECT_LEVEL)
            }
        }
    }

    // ------------------------------------------------------------------
    // Delete helpers
    // ------------------------------------------------------------------

    /**
     * Removes all dep libraries whose source roots sit under [depsUrl].
     *
     * **Must be called under a write lock** - use `edtWriteAction { deleteAllLibraries(...) }` in
     * production (see [drain]) or `WriteAction.run { deleteAllLibraries(...) }` in tests.
     */
    @VisibleForTesting
    internal fun deleteAllLibraries(depsUrl: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val prefixURI = URI(depsUrl)

        val depsLibraries = libraryTable.libraries.filter { library ->
            val urls = library.getUrls(OrderRootType.SOURCES)
            urls.isNotEmpty() && urls.all { url ->
                val relativeURI = prefixURI.relativize(URI(url))
                !relativeURI.isAbsolute && !relativeURI.toString().startsWith("../")
            }
        }

        depsLibraries.forEach { libraryTable.removeLibrary(it) }
    }

    /**
     * Removes the dep library with the given [depName].
     *
     * **Must be called under a write lock** - use `edtWriteAction { deleteLibrary(...) }` in
     * production (see [drain]) or `WriteAction.run { deleteLibrary(...) }` in tests.
     */
    @VisibleForTesting
    internal fun deleteLibrary(depName: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        libraryTable.getLibraryByName(depName)?.let { library ->
            libraryTable.removeLibrary(library)
        }
    }

    companion object {
        private const val DEBOUNCE_MS: Long = 250
        private val SOURCE_NAMES = setOf("c_src", "lib", "priv", "src")
        private val LOG = logger<MixDepsSyncService>()
    }
}

private fun Library.ModifiableModel.syncRoots(orderRootType: OrderRootType, desiredUrls: List<String>) {
    val desiredUrlSet = desiredUrls.toSet()

    for (existingUrl in getUrls(orderRootType)) {
        ProgressManager.checkCanceled()
        if (existingUrl !in desiredUrlSet) {
            removeRoot(existingUrl, orderRootType)
        }
    }

    val existingUrlSet = getUrls(orderRootType).toSet()
    for (desiredUrl in desiredUrls) {
        ProgressManager.checkCanceled()
        if (desiredUrl !in existingUrlSet) {
            addRoot(desiredUrl, orderRootType)
        }
    }
}
