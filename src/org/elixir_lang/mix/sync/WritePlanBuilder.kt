package org.elixir_lang.mix.sync

import com.intellij.openapi.application.readAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.impl.libraries.LibraryEx
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VfsUtilCore
import org.elixir_lang.mix.library.Kind

/**
 * Per-library diff to be applied during [applyWritePlan].
 *
 * [createWithKind] is true when the library does not yet exist in the table and must be created
 * with [Kind]. When false, the library exists and only the diff needs to be applied.
 */
internal data class LibraryWriteOp(
    val libraryName: String,
    val createWithKind: Boolean,
    val addClassUrls: List<String>,
    val removeClassUrls: List<String>,
    val addSourceUrls: List<String>,
    val removeSourceUrls: List<String>,
)

/**
 * Per-module dependency wiring to be applied during [applyWritePlan].
 *
 * Only the additions are recorded here - existing entries are never removed by the sync pipeline.
 * [addModuleDeps] contains module names whose dep-module is currently live; [addInvalidModuleDeps]
 * contains names whose dep-module is absent or disposed at plan-build time.  [addLibraryDeps] and
 * [addInvalidLibraryDeps] follow the same convention for project libraries.
 */
internal data class ModuleWriteOp(
    val moduleName: String,
    val addModuleDeps: Set<String>,
    val addInvalidModuleDeps: Set<String>,
    val addLibraryDeps: Set<String>,
    val addInvalidLibraryDeps: Set<String>,
    val addExcludeFolderUrls: List<String>,
)

/**
 * Immutable description of all model mutations required for one sync cycle.
 *
 * Produced by [buildWritePlan] under [readAction]; consumed by [applyWritePlan] under
 * [com.intellij.openapi.application.edtWriteAction].  Every field is a plain snapshot (strings,
 * primitive collections); no live model objects are retained across the suspend boundary.
 */
internal data class WritePlan(
    /** Library names to remove (from DeleteAll / DeleteOne scanning + legacy cleanup). */
    val librariesToRemove: List<String>,
    /** Diff-based library creates/updates; one op per library that actually changed. */
    val libraryWriteOps: List<LibraryWriteOp>,
    /** Names of libraries to create as empty placeholders (declared deps not yet fetched). */
    val placeholderLibraries: Set<String>,
    /** Per-module order-entry and exclude-folder additions. */
    val moduleWriteOps: List<ModuleWriteOp>,
    /** Legacy unscoped library names to remove when scoped replacements are created. */
    val legacyLibrariesToRemove: List<String>,
) {
    val isEmpty: Boolean
        get() = librariesToRemove.isEmpty() &&
            libraryWriteOps.isEmpty() &&
            placeholderLibraries.isEmpty() &&
            moduleWriteOps.isEmpty() &&
            legacyLibrariesToRemove.isEmpty()
}

/**
 * Snapshots the current project / library-table state under a [readAction] and computes the
 * minimal mutation set required to apply [syncPlan].
 *
 * **Threading contract:** runs entirely inside [readAction]; performs no write-lock acquisition
 * and no model mutations.  All output is an immutable snapshot safe to pass to [applyWritePlan]
 * across a suspend boundary.
 *
 * The diff computation handles:
 * - Delete candidates (DeleteAll / DeleteOne) matched against the table snapshot.
 * - Per-library root diffs (add / remove class and source roots), skipping unchanged libraries.
 * - Legacy-cleanup candidates (unscoped libraries superseded by a new scoped name).
 * - Placeholder library names (declared deps not yet fetched; no physical directory).
 * - Module order-entry additions (module deps + library deps + exclude folders).
 */
internal suspend fun buildWritePlan(project: Project, syncPlan: SyncPlan): WritePlan =
    readAction {
        if (project.isDisposed) return@readAction WritePlan(
            librariesToRemove = emptyList(),
            libraryWriteOps = emptyList(),
            placeholderLibraries = emptySet(),
            moduleWriteOps = emptyList(),
            legacyLibrariesToRemove = emptyList(),
        )
        buildWritePlanInCurrentContext(project, syncPlan)
    }

// ---------------------------------------------------------------------------
// Internal helpers (all requirements: must be called inside readAction)
// ---------------------------------------------------------------------------

/**
 * Core snapshot + diff logic.  Must only be called from within [readAction] or write context.
 */
private fun buildWritePlanInCurrentContext(project: Project, syncPlan: SyncPlan): WritePlan {
    val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

    // -----------------------------------------------------------------------
    // Snapshot - library-table state
    // -----------------------------------------------------------------------
    data class LibSnap(val isKind: Boolean, val classUrls: Set<String>, val sourceUrls: Set<String>)
    val libSnap: Map<String, LibSnap> = buildMap {
        for (lib in libraryTable.libraries) {
            val name = lib.name ?: continue
            put(
                name,
                LibSnap(
                    isKind = (lib as? LibraryEx)?.getKind() == Kind,
                    classUrls = lib.getUrls(OrderRootType.CLASSES).toSet(),
                    sourceUrls = lib.getUrls(OrderRootType.SOURCES).toSet(),
                )
            )
        }
    }

    // -----------------------------------------------------------------------
    // Step 1 - Compute librariesToRemove from DeleteAll / DeleteOne
    // -----------------------------------------------------------------------
    val librariesToRemove = mutableListOf<String>()

    for (deleteAll in syncPlan.deleteAlls) {
        ProgressManager.checkCanceled()
        // depsUrl is always "<contentRootUrl>/deps"; strip the suffix to derive scoped-name suffix.
        val contentRootUrl = deleteAll.depsUrl.removeSuffix("/deps")
        val scopedSuffix = " [$contentRootUrl]"
        // Append "/" so VfsUtilCore.isEqualOrAncestor is path-boundary-safe (Strategy 2 mirror).
        val depsPrefixUrl = if (deleteAll.depsUrl.endsWith("/")) deleteAll.depsUrl else "${deleteAll.depsUrl}/"

        for ((name, snap) in libSnap) {
            ProgressManager.checkCanceled()
            // Strategy 1: scoped-name + Kind (catches empty placeholders and class-only libs).
            val matchesScoped = name.endsWith(scopedSuffix) && snap.isKind
            // Strategy 2: source-root ancestor check (backwards-compat for legacy unscoped libs).
            val matchesSources = snap.sourceUrls.isNotEmpty() &&
                snap.sourceUrls.all { VfsUtilCore.isEqualOrAncestor(depsPrefixUrl, it) }
            if (matchesScoped || matchesSources) {
                librariesToRemove += name
            }
        }
    }

    for (deleteOne in syncPlan.deleteOnes) {
        ProgressManager.checkCanceled()
        if (libSnap.containsKey(deleteOne.libraryName)) {
            librariesToRemove += deleteOne.libraryName
        }
        // Legacy cleanup: if the scoped name differs from the dep name, also remove the legacy
        // unscoped library (only if it is a Mix-Kind library to avoid touching user libraries).
        if (deleteOne.depName != deleteOne.libraryName && libSnap[deleteOne.depName]?.isKind == true) {
            librariesToRemove += deleteOne.depName
        }
    }

    // Sweep orphaned unscoped Mix-Kind libraries (no " [" content-root marker in name).
    // Runs on every drain at negligible cost; removes stale entries from projects that were
    // configured with an older plugin version before root-scoped library naming was introduced.
    // Scoped replacements are created by the libraryWriteOps in Step 2 if a matching plan exists.
    for ((name, snap) in libSnap) {
        if (snap.isKind && " [" !in name && name !in librariesToRemove) {
            librariesToRemove += name
        }
    }

    // -----------------------------------------------------------------------
    // Step 2 - Compute libraryWriteOps and legacyLibrariesToRemove
    //
    // Libraries scheduled for removal (from Step 1) are treated as absent for diff purposes:
    // applyWritePlan removes them first, so if the same library is also in a libraryPlan we
    // must emit a createWithKind op to recreate it after deletion. Without this, a
    // DeleteOne+re-sync burst for the same dep would delete the library but never recreate it
    // because the diff against the pre-delete snapshot shows no changes.
    // -----------------------------------------------------------------------
    val librariesToRemoveSet = librariesToRemove.toHashSet()
    val libraryWriteOps = mutableListOf<LibraryWriteOp>()
    val legacyLibrariesToRemove = mutableListOf<String>()

    for (plan in syncPlan.libraryPlans) {
        ProgressManager.checkCanceled()
        // Treat libraries scheduled for removal as absent - they will be deleted before
        // libraryWriteOps are applied, so we must recreate them from scratch.
        val existing = libSnap[plan.libraryName]?.takeIf { plan.libraryName !in librariesToRemoveSet }
        val desiredClass = plan.classRootUrls.toSet()
        val desiredSource = plan.sourceRootUrls.toSet()

        if (existing != null) {
            // Diff against existing - only emit an op when something actually changes.
            val addClass = (desiredClass - existing.classUrls).toList()
            val removeClass = (existing.classUrls - desiredClass).toList()
            val addSource = (desiredSource - existing.sourceUrls).toList()
            val removeSource = (existing.sourceUrls - desiredSource).toList()
            if (addClass.isNotEmpty() || removeClass.isNotEmpty() ||
                addSource.isNotEmpty() || removeSource.isNotEmpty()
            ) {
                libraryWriteOps += LibraryWriteOp(
                    libraryName = plan.libraryName,
                    createWithKind = false,
                    addClassUrls = addClass,
                    removeClassUrls = removeClass,
                    addSourceUrls = addSource,
                    removeSourceUrls = removeSource,
                )
            }
            // No diff → no write op (library is already up to date).
        } else {
            // Library does not yet exist (or is scheduled for removal) - create with all roots.
            libraryWriteOps += LibraryWriteOp(
                libraryName = plan.libraryName,
                createWithKind = true,
                addClassUrls = plan.classRootUrls,
                removeClassUrls = emptyList(),
                addSourceUrls = plan.sourceRootUrls,
                removeSourceUrls = emptyList(),
            )
        }

        // Legacy cleanup: when the scoped name differs from the unscoped dep name, schedule
        // removal of any existing unscoped library that carries Mix-Kind to avoid duplicates.
        // Guard: only remove Kind-bearing libraries to preserve user-created libraries.
        if (plan.libraryName != plan.depName) {
            val legacy = libSnap[plan.depName]
            if (legacy != null && legacy.isKind) {
                legacyLibrariesToRemove += plan.depName
            }
        }
    }

    // -----------------------------------------------------------------------
    // Step 3 - Compute placeholderLibraries (missingLibraryDeps)
    //
    // A library is a "placeholder" if it is required by a module plan's libraryDeps but:
    //   - is NOT already scheduled for creation via libraryWriteOps, AND
    //   - does NOT currently exist (or will be removed by this plan).
    // -----------------------------------------------------------------------
    // Extend librariesToRemoveSet (computed before Step 2) with legacy cleanup candidates.
    val allLibrariesToRemoveSet = librariesToRemoveSet + legacyLibrariesToRemove
    val plannedLibraryNames = libraryWriteOps.mapTo(HashSet()) { it.libraryName }

    val placeholderLibraries: Set<String> = syncPlan.modulePlans
        .flatMap { it.libraryDeps }
        .filterNot { name ->
            // Will be created via a libraryWriteOp (with actual diff/roots).
            name in plannedLibraryNames ||
                // Already exists AND will NOT be removed by this plan.
                (libSnap.containsKey(name) && name !in allLibrariesToRemoveSet)
        }
        .toSet()

    // -----------------------------------------------------------------------
    // Step 4 - Compute moduleWriteOps
    //
    // For valid/invalid library determination (addLibraryDeps vs addInvalidLibraryDeps):
    // anticipate the post-operation library-table state so that newly-created libraries are
    // wired as valid order entries rather than invalid ones.
    //
    // Known limitation - narrow read→write race window:
    // Entries that already exist at snapshot time are OMITTED from the write op (see the
    // "continue" guards below). If a concurrent write action removes one of those entries
    // in the gap between this readAction completing and applyWritePlan's edtWriteAction
    // acquiring the write lock, the removed entry will not be restored during this cycle.
    //
    // This is an explicit design trade-off: carrying the full desired state (including
    // already-existing entries) would revert applyWritePlan from a diff applicator to a
    // full desired-state enforcer, re-introducing the write-lock pressure that the
    // refactor was designed to eliminate. The countermeasure is self-healing: the next
    // VFS event triggers a new drain that restores any entry removed in this window.
    // The practical likelihood of this race is negligible - it requires another write
    // action to modify module roots within the sub-millisecond async gap.
    // -----------------------------------------------------------------------
    // Set of library names that will exist after all write-plan operations complete:
    //   = current snapshot - to-remove + newly-created (write ops + placeholders)
    val futureLibraries: Set<String> =
        (libSnap.keys - allLibrariesToRemoveSet) + plannedLibraryNames + placeholderLibraries

    val moduleManager = ModuleManager.getInstance(project)
    val moduleNames = (syncPlan.modulePlans.map { it.moduleName } +
        syncPlan.libraryPlans.flatMap { it.excludeFolders }.map { it.moduleName })
        .toCollection(LinkedHashSet())
    val modulePlansByName = syncPlan.modulePlans.associateBy { it.moduleName }
    val excludeFoldersByModule = syncPlan.libraryPlans.flatMap { it.excludeFolders }
        .groupBy { it.moduleName }

    val moduleWriteOps = mutableListOf<ModuleWriteOp>()

    for (moduleName in moduleNames) {
        ProgressManager.checkCanceled()
        val module = moduleManager.findModuleByName(moduleName)?.takeIf { !it.isDisposed } ?: continue
        val rootManager = ModuleRootManager.getInstance(module)

        // Snapshot: existing order entries + exclude folders
        val existingModuleDeps = rootManager.orderEntries
            .filterIsInstance<ModuleOrderEntry>()
            .mapTo(HashSet()) { it.moduleName }
        val existingLibraryDeps = rootManager.orderEntries
            .filterIsInstance<LibraryOrderEntry>()
            .mapTo(HashSet()) { it.libraryName }
        val existingExcludeFolderUrls = rootManager.contentEntries
            .flatMapTo(HashSet()) { ce ->
                ce.excludeFolderUrls.filter { it.isNotEmpty() }
            }

        val modulePlan = modulePlansByName[moduleName]
        val addModuleDeps = mutableSetOf<String>()
        val addInvalidModuleDeps = mutableSetOf<String>()
        val addLibraryDeps = mutableSetOf<String>()
        val addInvalidLibraryDeps = mutableSetOf<String>()

        if (modulePlan != null) {
            for (depName in modulePlan.moduleDeps) {
                ProgressManager.checkCanceled()
                if (depName in existingModuleDeps) continue
                val depModule = moduleManager.findModuleByName(depName)
                if (depModule != null && !depModule.isDisposed) {
                    addModuleDeps += depName
                } else {
                    addInvalidModuleDeps += depName
                }
            }

            for (libName in modulePlan.libraryDeps) {
                ProgressManager.checkCanceled()
                if (libName in existingLibraryDeps) continue
                // Use the anticipated post-operation library state to decide valid vs invalid.
                // If the library will exist after all write-plan ops, wire it as a valid entry.
                if (libName in futureLibraries) {
                    addLibraryDeps += libName
                } else {
                    addInvalidLibraryDeps += libName
                }
            }
        }

        // Exclude folder additions (only for folders inside a known content entry).
        val addExcludeFolderUrls = (excludeFoldersByModule[moduleName].orEmpty())
            .filter { plan ->
                rootManager.contentEntries.any { entry ->
                    VfsUtilCore.isEqualOrAncestor(entry.url, plan.folderUrl)
                }
            }
            .map { it.folderUrl }
            .filterNot { it in existingExcludeFolderUrls }

        if (addModuleDeps.isNotEmpty() || addInvalidModuleDeps.isNotEmpty() ||
            addLibraryDeps.isNotEmpty() || addInvalidLibraryDeps.isNotEmpty() ||
            addExcludeFolderUrls.isNotEmpty()
        ) {
            moduleWriteOps += ModuleWriteOp(
                moduleName = moduleName,
                addModuleDeps = addModuleDeps,
                addInvalidModuleDeps = addInvalidModuleDeps,
                addLibraryDeps = addLibraryDeps,
                addInvalidLibraryDeps = addInvalidLibraryDeps,
                addExcludeFolderUrls = addExcludeFolderUrls,
            )
        }
    }

    return WritePlan(
        librariesToRemove = librariesToRemove,
        libraryWriteOps = libraryWriteOps,
        placeholderLibraries = placeholderLibraries,
        moduleWriteOps = moduleWriteOps,
        legacyLibrariesToRemove = legacyLibrariesToRemove,
    )
}
