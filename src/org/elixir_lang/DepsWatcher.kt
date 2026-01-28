package org.elixir_lang

import com.intellij.ide.projectView.impl.ProjectRootsUtil.isModuleContentRoot
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.elixir_lang.mix.Watcher
import org.elixir_lang.mix.library.Kind
import org.elixir_lang.util.DebouncedBulkFileListener
import org.elixir_lang.util.ElixirProjectDisposable
import org.elixir_lang.util.WriteActions.runWriteAction
import java.net.URI

/**
 * Watches the [project]'s `deps` folder for changes to `mix` deps
 *
 * * `deps/APPLICATION/{c_src,lib,priv,src}` - sources
 * * `_build/ENVIRONMENT/{consolidated,lib/APPLICATION/ebin` - classes
 */
class DepsWatcher(val project: Project) : DebouncedBulkFileListener(project.service<ElixirProjectDisposable>(), MERGE_DELAY_MS) {
    private val pendingLock = Any()
    private val pendingDeleteAllDepsUrls = mutableSetOf<String>()
    private val pendingDeleteLibraryNames = mutableSetOf<String>()
    private val pendingSyncDepsRoots = mutableSetOf<VirtualFile>()
    private val pendingSyncDepRoots = mutableSetOf<VirtualFile>()
    private var pendingSyncAll = false

    override fun enqueue(event: VFileEvent): Boolean =
        when (event) {
            is VFileDeleteEvent -> fileDeleted(event)
            is VFileCreateEvent -> fileCreated(event)
            else -> false
        }

    /**
     * If a file is deleted, it is need to determine whether it affect no deps, 1 dep, or all deps.
     *
     * * `deps` - deleteAllLibraries(deps)
     * * `deps/APPLICATION` - deleteLibrary(deps/APPLICATION)
     *
     * Other deletes cause syncs in [syncLibraries]
     */
    private fun fileDeleted(event: VFileDeleteEvent): Boolean {
        val file = event.file

        return when {
            file.name == "deps" && isModuleContentRoot(file.parent, project) -> queueDeleteAllLibraries(file.url)
            file.parent?.let { parent -> parent.name == "deps" && isModuleContentRoot(parent.parent, project) } == true ->
                queueDeleteLibrary(file.name)
            else -> syncLibraries(event)
        }
    }

    /**
     * If a file is created, i is needed to determined whether it affects no deps, 1 dep, or all deps
     *
     * * `deps` - syncLibraries(deps)
     * * `deps/APPLICATION` - syncLibrary(deps/APPLICATION)
     *
     * Other creates cause syncs in [syncLibraries]
     */
    private fun fileCreated(event: VFileCreateEvent): Boolean {
        val file = event.file ?: return false

        return if (file.name == "deps" && isModuleContentRoot(event.parent, project)) {
            queueSyncDepsRoot(file)
        } else if (event.parent.let { parent -> parent.name == "deps" && isModuleContentRoot(parent.parent, project) }) {
            queueSyncDepRoot(file)
        } else {
            syncLibraries(event)
        }
    }

    /**
     * Common syncs shared with [fileCreated] and [fileDeleted].
     *
     * * `_build` - syncLibraries(deps)
     * * `_build/ENVIRONMENT` - syncLibraries(deps)
     * * `_build/ENVIRONMENT/consolidated` - syncLibraries(deps)
     * * `_build/ENVIRONMENT/lib` - syncLibraries(deps)
     * * `deps/APPLICATION/{c_src,lib,priv,src}` - syncLibrary(deps/APPLICATION)
     * * `_build/ENVIRONMENT/lib/APPLICATION` - syncLibrary(deps/APPLICATION)
     * * `_build/ENVIRONMENT/lib/APPLICATION/ebin` - syncLibrary(deps/APPLICATION)
     */
    private fun syncLibraries(event: VFileEvent): Boolean {
        val file = event.file ?: return false

        return queueSyncFor(file)
    }

    fun syncLibraries(virtualFile: VirtualFile) {
        when (val request = syncRequestFor(virtualFile)) {
            SyncRequest.All -> scheduleSyncAll()
            is SyncRequest.Dep -> scheduleSyncDep(request.depRoot)
            null -> Unit
        }
    }

    private fun syncRequestFor(virtualFile: VirtualFile): SyncRequest? {
        val fileName = virtualFile.name
        val parent = virtualFile.parent ?: return null

        if (fileName == "_build" && isModuleContentRoot(parent, project)) {
            return SyncRequest.All
        }

        val grandParent = parent.parent ?: return null

        if (parent.name == "_build" && isModuleContentRoot(grandParent, project)) {
            return SyncRequest.All
        }

        val greatGrandParent = grandParent.parent ?: return null

        if (fileName in arrayOf("consolidated", "lib") && grandParent.name == "_build" && isModuleContentRoot(greatGrandParent, project)) {
            return SyncRequest.All
        }

        if (fileName in SOURCE_NAMES && grandParent.name == "deps" && isModuleContentRoot(greatGrandParent, project)) {
            return SyncRequest.Dep(parent)
        }

        val greatGreatGrandParent = greatGrandParent.parent ?: return null

        if (parent.name == "lib" && greatGrandParent.name == "_build" && isModuleContentRoot(greatGreatGrandParent, project)) {
            val dep = greatGreatGrandParent.findChild("deps")?.findChild(fileName)
            return dep?.let { SyncRequest.Dep(it) }
        }

        if (fileName == "ebin" && grandParent.name == "lib" && greatGreatGrandParent.name == "_build" &&
            isModuleContentRoot(greatGreatGrandParent.parent, project)) {
            val dep = greatGreatGrandParent.parent?.findChild("deps")?.findChild(parent.name)
            return dep?.let { SyncRequest.Dep(it) }
        }

        return null
    }

    private fun queueDeleteAllLibraries(depsUrl: String): Boolean {
        synchronized(pendingLock) {
            pendingDeleteAllDepsUrls.add(depsUrl)
        }

        return true
    }

    private fun queueDeleteLibrary(depName: String): Boolean {
        synchronized(pendingLock) {
            pendingDeleteLibraryNames.add(depName)
        }

        return true
    }

    private fun queueSyncDepsRoot(depsRoot: VirtualFile): Boolean {
        synchronized(pendingLock) {
            pendingSyncDepsRoots.add(depsRoot)
        }

        return true
    }

    private fun queueSyncDepRoot(depRoot: VirtualFile): Boolean {
        synchronized(pendingLock) {
            pendingSyncDepRoots.add(depRoot)
        }

        return true
    }

    private fun queueSyncFor(file: VirtualFile): Boolean {
        return when (val request = syncRequestFor(file)) {
            SyncRequest.All -> queueSyncAll()
            is SyncRequest.Dep -> queueSyncDepRoot(request.depRoot)
            null -> false
        }
    }

    private fun queueSyncAll(): Boolean {
        synchronized(pendingLock) {
            pendingSyncAll = true
            pendingSyncDepsRoots.clear()
            pendingSyncDepRoots.clear()
        }

        return true
    }

    override fun flushPending() {
        // TODO: SingleAlarm flush runs on EDT; consider moving library-table scans to a pooled thread.
        val deleteAllDepsUrls: Set<String>
        val deleteLibraryNames: Set<String>
        val syncAll: Boolean
        val syncDepsRoots: Set<VirtualFile>
        val syncDepRoots: Set<VirtualFile>

        synchronized(pendingLock) {
            deleteAllDepsUrls = pendingDeleteAllDepsUrls.toSet()
            deleteLibraryNames = pendingDeleteLibraryNames.toSet()
            syncAll = pendingSyncAll
            syncDepsRoots = pendingSyncDepsRoots.toSet()
            syncDepRoots = pendingSyncDepRoots.toSet()

            pendingDeleteAllDepsUrls.clear()
            pendingDeleteLibraryNames.clear()
            pendingSyncAll = false
            pendingSyncDepsRoots.clear()
            pendingSyncDepRoots.clear()
        }

        deleteAllDepsUrls.forEach { deleteAllLibraries(it) }
        deleteLibraryNames.forEach { deleteLibrary(it) }

        if (syncAll) {
            scheduleSyncAll()
        } else {
            scheduleSyncRequests(syncDepsRoots, syncDepRoots)
        }
    }

    private fun scheduleSyncAll() {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Elixir libraries", true) {
            override fun run(indicator: ProgressIndicator) {
                syncLibraries(indicator)
            }
        })
    }

    private fun scheduleSyncRequests(
        depsRoots: Set<VirtualFile>,
        depRoots: Set<VirtualFile>
    ) {
        val validDepsRoots = depsRoots.filter { it.isValid }.toSet()
        val validDepRoots = depRoots
            .filter { it.isValid }
            .filterNot { depRoot -> validDepsRoots.any { it == depRoot.parent } }
            .toSet()

        if (validDepsRoots.isEmpty() && validDepRoots.isEmpty()) {
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Elixir libraries", true) {
            override fun run(indicator: ProgressIndicator) {
                for (depsRoot in validDepsRoots) {
                    if (indicator.isCanceled) {
                        break
                    }

                    syncLibraries(depsRoot, indicator)
                }

                for (depRoot in validDepRoots) {
                    if (indicator.isCanceled) {
                        break
                    }

                    syncLibrary(depRoot, indicator)
                }
            }
        })
    }

    private fun scheduleSyncDep(depRoot: VirtualFile) {
        scheduleSyncRequests(emptySet(), setOf(depRoot))
    }

    fun syncLibraries(progressIndicator: ProgressIndicator) {
        ProjectRootManager
                .getInstance(project)
                .contentRootsFromAllModules
                .mapNotNull { it.findChild("deps") }
                .forEach { syncLibraries(it, progressIndicator) }
    }

    private fun syncLibrary(dep: VirtualFile, progressIndicator: ProgressIndicator) = syncLibraries(arrayOf(dep), progressIndicator)

    private fun syncLibraries(deps: VirtualFile, progressIndicator: ProgressIndicator) =
        syncLibraries(deps.children, progressIndicator)

    private fun syncLibraries(deps: Array<VirtualFile>, progressIndicator: ProgressIndicator) {
        if (deps.isNotEmpty()) {
            runWriteAction {
                val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

                syncLibraries(deps, libraryTable, progressIndicator)
            }

            for (module in ModuleManager.getInstance(project).modules) {
                if (progressIndicator.isCanceled) {
                    break
                }

                Watcher(project).syncLibraries(module, progressIndicator)
            }
        }
    }

    fun syncLibraries(
            deps: Array<VirtualFile>,
            libraryTable: LibraryTable,
            progressIndicator: ProgressIndicator
    ) {
        val libraryTableModifiableModel = libraryTable.modifiableModel

        for (dep in deps) {
            if (progressIndicator.isCanceled) {
                break
            }

            syncLibrary(dep, libraryTable, libraryTableModifiableModel, progressIndicator)
        }

        libraryTableModifiableModel.commit()
    }

    private fun syncLibrary(
            dep: VirtualFile,
            libraryTable: LibraryTable,
            libraryTableModifiableModel: LibraryTable.ModifiableModel,
            progressIndicator: ProgressIndicator
    ) {
        val depName = dep.name

        val library = libraryTable.getLibraryByName(depName)
                ?: libraryTableModifiableModel.createLibrary(dep.name, Kind)

        syncLibrary(library, dep, depName, progressIndicator)
    }

    private fun syncLibrary(library: Library, dep: VirtualFile, depName: String, progressIndicator: ProgressIndicator) {
        val libraryModifiableModel = library.modifiableModel

        ProjectRootManager
                .getInstance(project)
                .contentRootsFromAllModules
                .mapNotNull { it.findChild("_build") }
                .forEach { build ->
                    build
                            .children
                            .filter { it.isDirectory }
                            .forEach { environment ->
                                environment
                                        .children
                                        .filter { it.isDirectory }
                                        .forEach { environmentChild ->
                                            val environmentChildName = environmentChild.name

                                            if (environmentChildName == "consolidated") {
                                                libraryModifiableModel.addRoot(environmentChild, OrderRootType.CLASSES)
                                            } else if (environmentChildName == "lib") {
                                                environmentChild.findChild(depName)?.let { depEnvironmentLibrary ->
                                                    depEnvironmentLibrary.findChild("ebin")?.let { ebin ->
                                                        if (ebin.isDirectory) {
                                                            /* Mark build output as excluded when marking it as CLASSES, so that
                                                               dependency will show up in External Libraries AND be pushed out into
                                                               non-project results */
                                                            ModuleUtil
                                                                    .findModuleForFile(depEnvironmentLibrary, project)
                                                                    ?.let { module ->
                                                                        ModuleRootManager.getInstance(module).modifiableModel.apply {
                                                                            progressIndicator.text2 = "Excluding _build/lib/$depName/ebin from project so it is treated as an External Library"

                                                                            for (contentEntry in contentEntries) {
                                                                                if (progressIndicator.isCanceled) {
                                                                                    break
                                                                                }

                                                                                contentEntry.addExcludeFolder(depEnvironmentLibrary)
                                                                            }

                                                                            commit()
                                                                        }
                                                                    }

                                                            if (!progressIndicator.isCanceled) {
                                                                progressIndicator.text2 = "Adding _build/lib/$depName/ebin as a Classes root for $"
                                                                libraryModifiableModel.addRoot(ebin, OrderRootType.CLASSES)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                            }
                }

        for (child in dep.children) {
            if (progressIndicator.isCanceled) {
                break
            }

            if (child.isDirectory) {
                val childName = child.name

                if (childName in SOURCE_NAMES) {
                    progressIndicator.text2 = "Adding $childName as Source root for $depName"
                    libraryModifiableModel.addRoot(child, OrderRootType.SOURCES)
                }
            }
        }

        libraryModifiableModel.commit()
    }

    private fun deleteAllLibraries(depsUrl: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        val depsLibraries = libraryTable.libraries.filter { library ->
            // `getFile` won't return a `VirtualFile` that has been deleted, so need to use `getUrls`
            val urls = library.getUrls(OrderRootType.SOURCES)

            if (urls.isNotEmpty()) {
                val prefixURI = URI(depsUrl)

                urls.all { url ->
                    val uri = URI(url)
                    val relativeURI = prefixURI.relativize(uri)

                    !relativeURI.isAbsolute && !relativeURI.toString().startsWith("../")
                }
            } else {
                false
            }
        }

        if (depsLibraries.isNotEmpty()) {
            runWriteAction {
                depsLibraries.forEach { libraryTable.removeLibrary(it) }
            }
        }
    }

    private fun deleteLibrary(depName: String) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        libraryTable.getLibraryByName(depName)?.let { library ->
            runWriteAction {
                libraryTable.removeLibrary(library)
            }
        }
    }

}

private sealed class SyncRequest {
    object All : SyncRequest()
    data class Dep(val depRoot: VirtualFile) : SyncRequest()
}

private const val MERGE_DELAY_MS = 250
private val SOURCE_NAMES = arrayOf("c_src", "lib", "priv", "src")
