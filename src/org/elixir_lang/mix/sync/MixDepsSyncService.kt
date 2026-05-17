package org.elixir_lang.mix.sync

import com.google.common.annotations.VisibleForTesting
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
 * - [LOG] emits `debug`-level messages at drain-start and drain-complete with per-stage elapsed
 *   times: buildSyncPlan, buildWritePlan (read-phase snapshot + diff), and applyWritePlan
 *   (write-lock hold time - the key SLO metric).
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
                    supervisorScope {
                        val drainAttempt = async { drain() }

                        try {
                            drainAttempt.await()
                        } catch (
                            @Suppress("IncorrectCancellationExceptionHandling")
                            _: CancellationException
                        ) {
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

            val rawRequests: List<SyncRequest> = pendingRequests.getAndSet(emptySet()).toList()
            if (rawRequests.isEmpty()) return

            val requests = resolvePathShapedRequests(project, rawRequests)
            if (requests.isEmpty()) return

            val drainStartNs = System.nanoTime()
            LOG.debug("MixDepsSyncService: draining ${requests.size} request(s)")

            val coalescedRequests = coalesceRequests(requests)

            val buildSyncPlanStartNs = System.nanoTime()
            val syncPlan = withBackgroundProgress(project, "Syncing Elixir dependencies") {
                withContext(Dispatchers.Default) {
                    buildSyncPlan(project, coalescedRequests)
                }
            }
            val buildSyncPlanMs = (System.nanoTime() - buildSyncPlanStartNs) / 1_000_000

            var buildWritePlanMs = 0L
            var applyWritePlanMs = 0L
            var applyStats = ApplyStats(0, 0)

            if (!syncPlan.isEmpty) {
                val buildWritePlanStartNs = System.nanoTime()
                val writePlan = withBackgroundProgress(project, "Computing Elixir dependency changes") {
                    withContext(Dispatchers.Default) {
                        buildWritePlan(project, syncPlan)
                    }
                }
                buildWritePlanMs = (System.nanoTime() - buildWritePlanStartNs) / 1_000_000

                if (!writePlan.isEmpty) {
                    val applyWritePlanStartNs = System.nanoTime()
                    applyStats = withBackgroundProgress(project, "Applying Elixir dependency sync") {
                        applyWritePlan(project, writePlan)
                    }
                    applyWritePlanMs = (System.nanoTime() - applyWritePlanStartNs) / 1_000_000
                }
            }

            val totalMs = (System.nanoTime() - drainStartNs) / 1_000_000
            LOG.debug(
                "MixDepsSyncService: drain complete - ${requests.size} request(s) in ${totalMs}ms " +
                    "(buildSyncPlan=${buildSyncPlanMs}ms, " +
                    "buildWritePlan=${buildWritePlanMs}ms, " +
                    "applyWritePlan=${applyWritePlanMs}ms [write-lock hold], " +
                    "deleteAlls=${coalescedRequests.deleteAlls.size}, " +
                    "deleteOnes=${coalescedRequests.deleteOnes.size}, " +
                    "hasAll=${coalescedRequests.hasAll}, " +
                    "syncRoots=${coalescedRequests.syncRoots.size}, " +
                    "depsRoots=${coalescedRequests.depsRoots.size}, " +
                    "depRoots=${coalescedRequests.depRoots.size}, " +
                    "syncModules=${coalescedRequests.syncModuleNames.size}, " +
                    "libraryPlans=${syncPlan.libraryPlans.size}, " +
                    "modulePlans=${syncPlan.modulePlans.size}, " +
                    "librariesChanged=${applyStats.librariesChanged}, " +
                    "modulesChanged=${applyStats.modulesChanged})"
            )
        }
    }

    // ------------------------------------------------------------------
    // Per-module mix.exs resolution + dependency wiring
    // ------------------------------------------------------------------

    @VisibleForTesting
    internal suspend fun syncLibrariesForModule(module: Module) {
        val modulePlan = buildModuleDepsPlan(project, module.name, emptyList()) ?: return
        val syncPlan = SyncPlan(
            libraryPlans = deduplicateLibraryPlans(modulePlan.externalLibraryPlans),
            modulePlans = listOf(modulePlan),
        )
        if (!syncPlan.isEmpty) {
            val writePlan = buildWritePlan(project, syncPlan)
            if (!writePlan.isEmpty) {
                applyWritePlan(project, writePlan)
            }
        }
    }

    companion object {
        private const val DEBOUNCE_MS: Long = 250
        private val LOG = logger<MixDepsSyncService>()
    }
}

/**
 * Generates a deterministic, cross-platform, root-scoped library name for a Mix dep.
 *
 * The name is derived from [contentRootUrl] (a VirtualFile URL, always forward-slash) and
 * [depName]. Two content roots that each declare a dep named "phoenix" produce two distinct
 * library names, solving the cross-contamination between content roots that share the same dep name.
 *
 * **This is the single canonical naming helper - all production and test call sites MUST use it
 * rather than inlining string formats.**
 *
 * @param contentRootUrl  The VirtualFile URL of the content root that owns the dep (e.g.
 *   `"file:///project/my_app"`). Already forward-slash from VirtualFile.url; no normalisation
 *   needed.
 * @param depName  The Mix application/dep name (e.g. `"phoenix"`).
 * @return A scoped library name such as `"phoenix [file:///project/my_app]"`.
 */
@VisibleForTesting
internal fun scopedDepLibraryName(contentRootUrl: String, depName: String): String =
    "$depName [$contentRootUrl]"

// ---------------------------------------------------------------------------
// Internal top-level plan data classes
//
// Declared at file scope (internal) so WritePlanBuilder.kt and WritePlanApplicator.kt can access
// them as inputs to their respective phases without crossing visibility boundaries.
// ---------------------------------------------------------------------------

internal data class DeleteAllPlan(val depsUrl: String)

internal data class DeleteOnePlan(val depName: String, val contentRootUrl: String?) {
    /**
     * The library name to delete: scoped if [contentRootUrl] is known, otherwise the legacy
     * unscoped dep name (for backwards-compatible deletion of previous version libraries).
     */
    val libraryName: String
        get() = if (contentRootUrl != null) scopedDepLibraryName(contentRootUrl, depName) else depName
}

internal data class ExcludeFolderPlan(
    val moduleName: String,
    val folderUrl: String,
)

internal data class LibraryRootsPlan(
    /** The URL of the content root under which this dep lives (e.g. `file:///project/my_app`). */
    val contentRootUrl: String,
    val depName: String,
    val classRootUrls: List<String>,
    val sourceRootUrls: List<String>,
    val excludeFolders: List<ExcludeFolderPlan>,
) {
    /**
     * The scoped project-library name for this dep.
     * Two content roots with the same dep name produce distinct [libraryName] values,
     * preventing cross-contamination between unrelated projects.
     */
    val libraryName: String get() = scopedDepLibraryName(contentRootUrl, depName)
}

internal data class ModuleDepsPlan(
    val moduleName: String,
    val moduleDeps: Set<String>,
    val libraryDeps: Set<String>,
    val externalLibraryPlans: List<LibraryRootsPlan>,
)

internal data class SyncPlan(
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
