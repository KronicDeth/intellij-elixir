package org.elixir_lang.mix

import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiManager
import org.elixir_lang.DepsWatcher
import org.elixir_lang.mix.library.Kind
import org.elixir_lang.mix.watcher.TransitiveResolution.transitiveResolution
import org.elixir_lang.util.DebouncedBulkFileListener
import org.elixir_lang.util.ElixirProjectDisposable
import org.elixir_lang.util.WriteActions.runWriteAction
import org.elixir_lang.mix.Project as MixProject

/**
 * Watches the [Module]'s `mix.exs` for changes to the `deps`, so that [com.intellij.openapi.roots.libraries.Library]
 * created by [org.elixir_lang.DepsWatcher] can be added to the correct [Module].
 */
class Watcher(private val project: Project) : DebouncedBulkFileListener(project.service<ElixirProjectDisposable>(), MERGE_DELAY_MS) {
    private val pendingLock = Any()
    private val pendingModules = mutableSetOf<Module>()

    override fun enqueue(event: VFileEvent): Boolean =
        when (event) {
            is VFileContentChangeEvent -> contentsChanged(event)
            else -> false
        }

    private fun contentsChanged(event: VFileContentChangeEvent): Boolean {
        if (event.file.name != MixProject.MIX_EXS) {
            return false
        }

        val module = ModuleUtil.findModuleForFile(event.file, project) ?: return false
        val eventFileParent = event.file.parent
        val shouldSync =
            ModuleRootManager
                .getInstance(module)
                .contentRoots
                .any { contentRoot -> contentRoot == eventFileParent }

        return if (shouldSync) {
            queueModuleSync(module)
        } else {
            false
        }
    }

    private fun queueModuleSync(module: Module): Boolean {
        synchronized(pendingLock) {
            pendingModules.add(module)
        }

        return true
    }

    override fun flushPending() {
        // TODO: Consider chunking module syncs if many modules change at once.
        val modules: Set<Module>

        synchronized(pendingLock) {
            modules = pendingModules.toSet()
            pendingModules.clear()
        }

        val activeModules = modules.filterNot { it.isDisposed }

        if (activeModules.isEmpty()) {
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(
            project,
            "Syncing Libraries from mix.exs",
            true
        ) {
            override fun run(indicator: ProgressIndicator) {
                for (module in activeModules) {
                    if (indicator.isCanceled) {
                        break
                    }

                    syncLibraries(module, indicator)
                }
            }
        })
    }

    fun syncLibraries(module: Module, progressIndicator: ProgressIndicator) {
        ModuleRootManager
            .getInstance(module)
            .contentRoots
            .let { transitiveResolution(project, PsiManager.getInstance(project), progressIndicator, *it) }
            .let { deps -> syncLibraries(module, deps, progressIndicator) }
    }

    private fun syncLibraries(module: Module, deps: Collection<Dep>, progressIndicator: ProgressIndicator) {
        if (deps.isNotEmpty()) {
            runWriteAction {
                val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
                val moduleManager = ModuleManager.getInstance(project)

                if (!module.isDisposed) {
                    val moduleRootManager = ModuleRootManager.getInstance(module)

                    for (dep in deps) {
                        if (progressIndicator.isCanceled) {
                            break
                        }

                        val depName = dep.application

                        when (dep.type) {
                            Dep.Type.MODULE -> {
                                progressIndicator.text2 =
                                    "Adding $depName Module as dependency of ${module.name} Module"

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
                                progressIndicator.text2 =
                                    "Adding $depName Library as dependency of ${module.name} Module"

                                val depLibrary = libraryTable.getLibraryByName(depName)

                                if (depLibrary != null) {
                                    if (moduleRootManager.orderEntries.none { it is LibraryOrderEntry && it.libraryName == depName }) {
                                        ModuleRootModificationUtil.addDependency(module, depLibrary)
                                    }
                                } else {
                                    val libraryTableModifiableModule = libraryTable.modifiableModel

                                    val invalidLibrary = libraryTableModifiableModule.createLibrary(depName, Kind)

                                    libraryTableModifiableModule.commit()

                                    moduleRootManager.modifiableModel.run {
                                        addLibraryEntry(invalidLibrary)
                                        commit()
                                    }
                                }

                            }
                        }
                    }

                    syncExternalPathLibraries(deps, progressIndicator)
                }
            }
        }
    }

    private fun syncExternalPathLibraries(deps: Collection<Dep>, progressIndicator: ProgressIndicator) {
        val projectRootManager = ProjectRootManager.getInstance(project)
        val externalPaths = deps.externalPaths(project, projectRootManager)

        if (externalPaths.isNotEmpty()) {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

            DepsWatcher(project).syncLibraries(externalPaths, libraryTable, progressIndicator)
        }
    }
}

private const val MERGE_DELAY_MS = 250

private fun Collection<Dep>.externalPaths(
    project: Project,
    projectRootManager: ProjectRootManager
): Array<VirtualFile> {
    val projectFileIndex = projectRootManager.fileIndex

    return this.mapNotNull { it.externalPath(project, projectFileIndex) }.toTypedArray()
}

private fun Dep.externalPath(project: Project, projectFileIndex: ProjectFileIndex): VirtualFile? =
    virtualFile(project)?.let { virtualFile ->
        if (projectFileIndex.getContentRootForFile(virtualFile) == null &&
            !projectFileIndex.isInLibrary(virtualFile) &&
            !projectFileIndex.isExcluded(virtualFile)
        ) {
            virtualFile
        } else {
            null
        }
    }
