package org.elixir_lang.mix.sync

import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VfsUtilCore
import org.elixir_lang.mix.library.Kind

/**
 * Counts of model objects changed during one [applyWritePlan] call.  Used for drain-complete
 * logging so write-lock hold time is correlated with the amount of work done.
 */
internal data class ApplyStats(val librariesChanged: Int, val modulesChanged: Int)

/**
 * Applies [writePlan] to the project model inside a single [edtWriteAction].
 *
 * **Threading contract:** the [edtWriteAction] block performs ONLY model mutations.  No VFS child
 * enumeration, PSI resolution, [com.intellij.openapi.module.ModuleUtil.findModuleForFile],
 * [com.intellij.openapi.roots.ProjectRootManager.contentRoots], or transitive
 * dependency discovery runs inside the write lock.  All scanning was done by [buildWritePlan] in
 * a prior [com.intellij.openapi.application.readAction].
 *
 * Stale-plan resilience: if the project is disposed at entry, or a target module is disposed
 * during iteration, the operation is skipped gracefully without throwing.
 *
 * Known limitation - narrow read→write race window: [ModuleWriteOp] only carries entries that
 * were *missing* at [buildWritePlan] snapshot time.  If a concurrent write action removes an
 * already-existing entry in the gap between the read phase and this write phase, it will not
 * be restored during this cycle.  Self-healing occurs on the next VFS-triggered drain.  This
 * is the deliberate cost of keeping write-lock hold time minimal; see the Step-4 comment in
 * [buildWritePlan] for the full rationale.
 */
internal suspend fun applyWritePlan(project: Project, writePlan: WritePlan): ApplyStats {
    var librariesChanged = 0
    var modulesChanged = 0

    edtWriteAction {
        if (project.isDisposed) return@edtWriteAction

        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        commitOrDispose(
            libraryTable.modifiableModel,
            { it.isChanged },
            { it.commit() },
            { it.dispose() },
        ) { tableModel ->
            // ------------------------------------------------------------------
            // Step 1: Remove libraries (from DeleteAll / DeleteOne + legacy cleanup).
            // Exact name lookup - no filter iteration inside the write lock.
            // ------------------------------------------------------------------
            val allToRemove = writePlan.librariesToRemove + writePlan.legacyLibrariesToRemove
            for (name in allToRemove) {
                if (project.isDisposed) break
                ProgressManager.checkCanceled()
                tableModel.getLibraryByName(name)?.let { lib ->
                    tableModel.removeLibrary(lib)
                    librariesChanged++
                }
            }

            // ------------------------------------------------------------------
            // Step 2: Create or update libraries using pre-computed root diffs.
            // One modifiable model → one commit() for all library changes.
            // ------------------------------------------------------------------
            for (op in writePlan.libraryWriteOps) {
                if (project.isDisposed) break
                ProgressManager.checkCanceled()
                val library = if (op.createWithKind) {
                    tableModel.createLibrary(op.libraryName, Kind)
                } else {
                    // Library should exist (was in snapshot); fall back to create if a concurrent
                    // removal happened between the read phase and now.
                    tableModel.getLibraryByName(op.libraryName)
                        ?: tableModel.createLibrary(op.libraryName, Kind)
                }
                applyLibraryRootsDiff(library, op)
                librariesChanged++
            }

            // ------------------------------------------------------------------
            // Step 3: Create placeholder libraries for declared-but-unfetched deps.
            // Only create if not already present after the operations above.
            // ------------------------------------------------------------------
            for (name in writePlan.placeholderLibraries) {
                if (project.isDisposed) break
                ProgressManager.checkCanceled()
                if (tableModel.getLibraryByName(name) == null) {
                    tableModel.createLibrary(name, Kind)
                    librariesChanged++
                }
            }
        }

        if (project.isDisposed) return@edtWriteAction

        // ------------------------------------------------------------------
        // Step 4: Wire module dependencies and exclude folders.
        // One modifiable root model per module; all changes accumulated before commit.
        // Library lookups use the live table after Step 1-3 committed above.
        // ------------------------------------------------------------------
        val moduleManager = ModuleManager.getInstance(project)

        for (op in writePlan.moduleWriteOps) {
            if (project.isDisposed) break
            ProgressManager.checkCanceled()
            val module = moduleManager.findModuleByName(op.moduleName)
                ?.takeIf { !it.isDisposed }
                ?: continue

            val modifiableModel = ModuleRootManager.getInstance(module).modifiableModel
            if (commitOrDispose(
                    modifiableModel,
                    { it.isChanged },
                    { it.commit() },
                    { it.dispose() },
                ) { model ->
                    applyModuleWriteOp(model, op, moduleManager, libraryTable)
                }
            ) {
                modulesChanged++
            }
        }
    }

    return ApplyStats(librariesChanged = librariesChanged, modulesChanged = modulesChanged)
}

// ---------------------------------------------------------------------------
// Private helpers - must only be called under a write lock
// ---------------------------------------------------------------------------

/**
 * Applies the pre-computed root diff from [op] to [library] using a dedicated library modifiable
 * model.  Skips commit (and disposes instead) when no roots actually changed.
 */
private fun applyLibraryRootsDiff(library: Library, op: LibraryWriteOp) {
    commitOrDispose(
        library.modifiableModel,
        { it.isChanged },
        { it.commit() },
        { it.dispose() },
    ) { libraryModel ->
        for (url in op.removeClassUrls) {
            ProgressManager.checkCanceled()
            libraryModel.removeRoot(url, OrderRootType.CLASSES)
        }
        for (url in op.addClassUrls) {
            ProgressManager.checkCanceled()
            libraryModel.addRoot(url, OrderRootType.CLASSES)
        }
        for (url in op.removeSourceUrls) {
            ProgressManager.checkCanceled()
            libraryModel.removeRoot(url, OrderRootType.SOURCES)
        }
        for (url in op.addSourceUrls) {
            ProgressManager.checkCanceled()
            libraryModel.addRoot(url, OrderRootType.SOURCES)
        }
    }
}

/**
 * Wires the module deps, library deps, and exclude folders described in [op] into
 * [modifiableModel].  All diffs were pre-computed by [buildWritePlan]; the only scanning
 * done here is a cheap live-model snapshot of existing order entries and exclude-folder URLs
 * to guard against duplicates in case a concurrent write action added entries after the
 * read-phase snapshot.  If a module dep-module is now disposed (race between read and
 * write phases), it is automatically demoted to an invalid entry.
 */
private fun applyModuleWriteOp(
    modifiableModel: ModifiableRootModel,
    op: ModuleWriteOp,
    moduleManager: ModuleManager,
    libraryTable: com.intellij.openapi.roots.libraries.LibraryTable,
) {
    // Revalidate the live model state at apply time to guard against a concurrent write action
    // that added entries between buildWritePlan (readAction) and applyWritePlan (edtWriteAction).
    // Without this check, addModuleOrderEntry / addLibraryEntry / addExcludeFolder would create
    // duplicate entries because those APIs always append rather than add-if-absent.
    val liveModuleDeps = modifiableModel.orderEntries
        .filterIsInstance<ModuleOrderEntry>()
        .mapTo(HashSet()) { it.moduleName }
    val liveLibraryDeps = modifiableModel.orderEntries
        .filterIsInstance<LibraryOrderEntry>()
        .mapTo(HashSet()) { it.libraryName }
    val liveExcludeFolderUrls = modifiableModel.contentEntries
        .flatMapTo(HashSet()) { it.excludeFolderUrls.filter(String::isNotEmpty) }

    // Module deps - re-validate existence at apply time to handle the read→write race.
    for (depName in op.addModuleDeps) {
        ProgressManager.checkCanceled()
        if (depName in liveModuleDeps) continue
        val depModule = moduleManager.findModuleByName(depName)
        if (depModule != null && !depModule.isDisposed) {
            modifiableModel.addModuleOrderEntry(depModule)
        } else {
            // Module was present at build time but gone now; fall back to invalid entry.
            modifiableModel.addInvalidModuleEntry(depName)
        }
    }
    for (depName in op.addInvalidModuleDeps) {
        ProgressManager.checkCanceled()
        if (depName in liveModuleDeps) continue
        modifiableModel.addInvalidModuleEntry(depName)
    }

    // Library deps - re-validate existence using the live table (post-Step-1-3 commit).
    for (libName in op.addLibraryDeps) {
        ProgressManager.checkCanceled()
        if (libName in liveLibraryDeps) continue
        val lib = libraryTable.getLibraryByName(libName)
        if (lib != null) {
            modifiableModel.addLibraryEntry(lib)
        } else {
            // Library was expected to exist (per plan) but is absent; add as invalid
            // rather than silently dropping so the developer can see the broken reference.
            modifiableModel.addInvalidLibrary(libName, LibraryTablesRegistrar.PROJECT_LEVEL)
        }
    }
    for (libName in op.addInvalidLibraryDeps) {
        ProgressManager.checkCanceled()
        if (libName in liveLibraryDeps) continue
        modifiableModel.addInvalidLibrary(libName, LibraryTablesRegistrar.PROJECT_LEVEL)
    }

    // Exclude folders - guard against duplicates; addExcludeFolder always appends.
    for (folderUrl in op.addExcludeFolderUrls) {
        ProgressManager.checkCanceled()
        if (folderUrl in liveExcludeFolderUrls) continue
        for (contentEntry in modifiableModel.contentEntries) {
            if (VfsUtilCore.isEqualOrAncestor(contentEntry.url, folderUrl)) {
                contentEntry.addExcludeFolder(folderUrl)
                break
            }
        }
    }
}

/**
 * Commits [model] if [isChanged] returns true, otherwise disposes it.  Returns `true` when the
 * model was committed.
 *
 * Eliminates the repeated `var committed = false; try { ...; if (isChanged) { commit(); committed = true } }
 * finally { if (!committed) dispose() }` pattern at every modifiable-model call site.
 *
 * Declared `private inline` so the lambda [block] is inlined at each call site - no lambda
 * object is allocated and captured variables are not boxed.
 */
private inline fun <M> commitOrDispose(
    model: M,
    isChanged: (M) -> Boolean,
    commit: (M) -> Unit,
    dispose: (M) -> Unit,
    block: (M) -> Unit,
): Boolean {
    var committed = false
    try {
        block(model)
        if (isChanged(model)) {
            commit(model)
            committed = true
        }
    } finally {
        if (!committed) dispose(model)
    }
    return committed
}
