package org.elixir_lang.mix.sync

import com.google.common.annotations.VisibleForTesting
import com.intellij.openapi.application.edtWriteAction
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
import com.intellij.platform.util.progress.reportSequentialProgress
import com.intellij.psi.PsiManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.elixir_lang.mix.Dep
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
            val requests: List<SyncRequest> = pendingRequests.getAndSet(emptySet()).toList()
            if (requests.isEmpty()) return

            val drainStartNs = System.nanoTime()
            LOG.debug("MixDepsSyncService: draining ${requests.size} request(s)")

            // Coalesce: separate into typed buckets.
            val deleteAlls = requests.filterIsInstance<SyncRequest.DeleteAll>()
            val deleteOnes = requests.filterIsInstance<SyncRequest.DeleteOne>()
            val hasAll = requests.any { it is SyncRequest.All }
            // URLs of every tree being deleted - used to suppress syncs for the same tree.
            // A DepsRoot or DepRoot whose path falls under a DeleteAll URL is dropped:
            // the delete will already remove those libraries, and a sync against a
            // (possibly deleted) tree immediately after would race or re-populate incorrectly.
            val deleteAllUrls: Set<String> = deleteAlls.map { it.depsUrl }.toSet()
            val depsRoots = if (hasAll) emptyList() else {
                requests.filterIsInstance<SyncRequest.DepsRoot>()
                    .filter { dr -> deleteAllUrls.none { url -> dr.depsRoot.url == url } }
            }
            val depRoots = if (hasAll) emptyList() else {
                // Drop any DepRoot whose parent DepsRoot is already being synced.
                val depsRootUrls = depsRoots.map { it.depsRoot.url }.toSet()
                requests.filterIsInstance<SyncRequest.DepRoot>()
                    .filter { r -> depsRootUrls.none { url -> r.depRoot.parent?.url == url } }
                    // Drop any DepRoot whose parent is being deleted by a DeleteAll.
                    .filter { r -> deleteAllUrls.none { url -> r.depRoot.parent?.url == url } }
            }
            val syncModules = requests.filterIsInstance<SyncRequest.SyncModule>()
                .map { it.module }
                .filter { !it.isDisposed }
                .toSet()

            // Execute deletes first so that a delete followed by a re-sync in the same drain
            // does not re-populate libraries that were intentionally removed.
            if (deleteAlls.isNotEmpty() || deleteOnes.isNotEmpty()) {
                withBackgroundProgress(project, "Removing Elixir dep libraries") {
                    // Single edtWriteAction batch: groups all deletes into one EDT round-trip.
                    edtWriteAction {
                        for (req in deleteAlls) {
                            if (!project.isDisposed) deleteAllLibraries(req.depsUrl)
                        }
                        for (req in deleteOnes) {
                            if (!project.isDisposed) deleteLibrary(req.depName)
                        }
                    }
                }
            }

            // Execute dep-tree syncs.
            if (hasAll) {
                executeSyncAll()
            } else if (depsRoots.isNotEmpty() || depRoots.isNotEmpty()) {
                executeSyncRequests(
                    depsRoots.map { it.depsRoot }.filter { it.isValid }.toSet(),
                    depRoots.map { it.depRoot }.filter { it.isValid }.toSet()
                )
            }

            // Execute per-module mix.exs re-syncs.
            if (syncModules.isNotEmpty()) {
                withBackgroundProgress(project, "Syncing Libraries from mix.exs") {
                    withContext(Dispatchers.Default) {
                        reportSequentialProgress(syncModules.size) { reporter ->
                            for (module in syncModules) {
                                reporter.itemStep("Syncing ${module.name}") {
                                    ProgressManager.checkCanceled()
                                    if (!module.isDisposed && !project.isDisposed) {
                                        syncLibrariesForModule(module)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val elapsedMs = (System.nanoTime() - drainStartNs) / 1_000_000
            LOG.debug(
                "MixDepsSyncService: drain complete - ${requests.size} request(s) in ${elapsedMs}ms " +
                    "(deleteAlls=${deleteAlls.size}, deleteOnes=${deleteOnes.size}, " +
                    "hasAll=$hasAll, depsRoots=${depsRoots.size}, depRoots=${depRoots.size}, " +
                    "syncModules=${syncModules.size})"
            )
        }
    }

    // ------------------------------------------------------------------
    // Dep-tree sync execution
    // ------------------------------------------------------------------

    private suspend fun executeSyncAll() {
        withBackgroundProgress(project, "Syncing Elixir libraries") {
            withContext(Dispatchers.Default) {
                syncAllLibraries()
            }
        }
    }

    private suspend fun executeSyncRequests(
        depsRoots: Set<VirtualFile>,
        depRoots: Set<VirtualFile>
    ) {
        if (depsRoots.isEmpty() && depRoots.isEmpty()) return

        val totalWork = depsRoots.size + depRoots.size
        withBackgroundProgress(project, "Syncing Elixir libraries") {
            withContext(Dispatchers.Default) {
                reportSequentialProgress(totalWork) { reporter ->
                    for (depsRoot in depsRoots) {
                        reporter.itemStep("Syncing deps under ${depsRoot.name}") {
                            ProgressManager.checkCanceled()
                            syncLibrariesUnderDepsRoot(depsRoot)
                        }
                    }
                    for (depRoot in depRoots) {
                        reporter.itemStep("Syncing dep ${depRoot.name}") {
                            ProgressManager.checkCanceled()
                            syncSingleDepRoot(depRoot)
                        }
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Dep-tree sync helpers
    // ------------------------------------------------------------------

    private suspend fun syncAllLibraries() {
        ProjectRootManager
            .getInstance(project)
            .contentRootsFromAllModules
            .mapNotNull { it.findChild("deps") }
            .forEach { syncLibrariesUnderDepsRoot(it) }
    }

    private suspend fun syncLibrariesUnderDepsRoot(depsRoot: VirtualFile) {
        syncLibrariesArray(depsRoot.children)
    }

    private suspend fun syncSingleDepRoot(depRoot: VirtualFile) {
        syncLibrariesArray(arrayOf(depRoot))
    }

    private suspend fun syncLibrariesArray(deps: Array<VirtualFile>) {
        if (deps.isEmpty()) return

        edtWriteAction {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            syncLibraries(deps, libraryTable)
        }

        // Preserve all-module fan-out: after syncing the project-level library table,
        // wire the newly synced libraries into each module's order entries via mix.exs PSI.
        for (module in ModuleManager.getInstance(project).modules) {
            if (project.isDisposed) break
            if (!module.isDisposed) syncLibrariesForModule(module)
        }
    }

    // ------------------------------------------------------------------
    // Per-module mix.exs resolution + dependency wiring
    // ------------------------------------------------------------------

    @VisibleForTesting
    internal suspend fun syncLibrariesForModule(module: Module) {
        val psiManager = PsiManager.getInstance(project)
        val indicator = EmptyProgressIndicator()
        val contentRoots = ModuleRootManager.getInstance(module).contentRoots
        val deps = transitiveResolution(project, psiManager, indicator, *contentRoots)
        if (deps.isNotEmpty()) {
            syncModuleDeps(module, deps)
        }
    }

    private suspend fun syncModuleDeps(module: Module, deps: Collection<Dep>) {
        if (deps.isEmpty()) return

        edtWriteAction {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val moduleManager = ModuleManager.getInstance(project)

            if (!module.isDisposed) {
                val moduleRootManager = ModuleRootManager.getInstance(module)

                for (dep in deps) {
                    if (project.isDisposed) break
                    val depName = dep.application

                    when (dep.type) {
                        Dep.Type.MODULE -> {
                            val depModule = moduleManager.findModuleByName(depName)
                            if (depModule != null) {
                                if (!moduleRootManager.isDependsOn(depModule)) {
                                    ModuleRootModificationUtil.addDependency(module, depModule)
                                }
                            } else {
                                moduleRootManager.modifiableModel.run {
                                    addInvalidModuleEntry(depName)
                                    commit()
                                }
                            }
                        }

                        Dep.Type.LIBRARY -> {
                            val depLibrary = libraryTable.getLibraryByName(depName)
                            if (depLibrary != null) {
                                if (moduleRootManager.orderEntries.none {
                                        it is LibraryOrderEntry && it.libraryName == depName
                                    }) {
                                    ModuleRootModificationUtil.addDependency(module, depLibrary)
                                }
                            } else {
                                val libraryTableModifiableModel = libraryTable.modifiableModel
                                val invalidLibrary = libraryTableModifiableModel.createLibrary(depName, Kind)
                                libraryTableModifiableModel.commit()
                                moduleRootManager.modifiableModel.run {
                                    addLibraryEntry(invalidLibrary)
                                    commit()
                                }
                            }
                        }
                    }
                }

                syncExternalPathLibraries(deps)
            }
        }
    }

    /** Syncs libraries for dep paths that live outside this project's content roots. */
    private fun syncExternalPathLibraries(deps: Collection<Dep>) {
        val projectRootManager = ProjectRootManager.getInstance(project)
        val projectFileIndex = projectRootManager.fileIndex
        val externalPaths = deps.mapNotNull { dep ->
            dep.virtualFile(project)?.let { vf ->
                if (projectFileIndex.getContentRootForFile(vf) == null &&
                    !projectFileIndex.isInLibrary(vf) &&
                    !projectFileIndex.isExcluded(vf)
                ) vf else null
            }
        }.toTypedArray()

        if (externalPaths.isNotEmpty()) {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            syncLibraries(externalPaths, libraryTable)
        }
    }

    // ------------------------------------------------------------------
    // Library-table sync (per-dep class/source root population)
    // ------------------------------------------------------------------

    @VisibleForTesting
    internal fun syncLibraries(deps: Array<VirtualFile>, libraryTable: LibraryTable) {
        val libraryTableModifiableModel = libraryTable.modifiableModel

        for (dep in deps) {
            if (project.isDisposed) break

            val depName = dep.name
            val library = libraryTable.getLibraryByName(depName)
                ?: libraryTableModifiableModel.createLibrary(dep.name, Kind)

            syncLibraryRoots(library, dep, depName)
        }

        libraryTableModifiableModel.commit()
    }

    private fun syncLibraryRoots(library: Library, dep: VirtualFile, depName: String) {
        val libraryModifiableModel = library.modifiableModel

        libraryModifiableModel.clearRoots(OrderRootType.CLASSES)
        libraryModifiableModel.clearRoots(OrderRootType.SOURCES)

        ProjectRootManager
            .getInstance(project)
            .contentRootsFromAllModules
            .mapNotNull { it.findChild("_build") }
            .forEach { build ->
                build.children
                    .filter { it.isDirectory }
                    .forEach { environment ->
                        environment.children
                            .filter { it.isDirectory }
                            .forEach { environmentChild ->
                                when (environmentChild.name) {
                                    "consolidated" ->
                                        libraryModifiableModel.addRoot(environmentChild, OrderRootType.CLASSES)
                                    "lib" ->
                                        environmentChild.findChild(depName)?.let { depEnvLib ->
                                            depEnvLib.findChild("ebin")?.let { ebin ->
                                                if (ebin.isDirectory) {
                                                    ModuleUtil
                                                        .findModuleForFile(depEnvLib, project)
                                                    ?.let { module ->
                                                        ModuleRootManager.getInstance(module).modifiableModel.apply {
                                                            for (contentEntry in contentEntries) {
                                                                val contentRoot = contentEntry.file ?: continue
                                                                // Only exclude under content entries that actually
                                                                // contain depEnvLib - cross-test or umbrella roots
                                                                // may be registered on the same module and would
                                                                // cause IllegalStateException if used here.
                                                                if (VfsUtilCore.isAncestor(contentRoot, depEnvLib, false)) {
                                                                    contentEntry.addExcludeFolder(depEnvLib)
                                                                }
                                                            }
                                                            commit()
                                                        }
                                                    }
                                                    libraryModifiableModel.addRoot(ebin, OrderRootType.CLASSES)
                                                }
                                            }
                                        }
                                }
                            }
                    }
            }

        for (child in dep.children) {
            if (project.isDisposed) break
            if (child.isDirectory && child.name in SOURCE_NAMES) {
                libraryModifiableModel.addRoot(child, OrderRootType.SOURCES)
            }
        }

        libraryModifiableModel.commit()
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

// Extension to clear all roots of a given type from a Library.ModifiableModel.
private fun Library.ModifiableModel.clearRoots(orderRootType: OrderRootType) {
    getUrls(orderRootType).forEach { removeRoot(it, orderRootType) }
}
