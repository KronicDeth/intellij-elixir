package org.elixir_lang.mix

import com.intellij.openapi.application.ApplicationManager
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
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiManager
import org.elixir_lang.DepsWatcher
import org.elixir_lang.mix.library.Kind
import org.elixir_lang.mix.watcher.TransitiveResolution.transitiveResolution

/**
 * Watches the [module]'s `mix.exs` for changes to the `deps`, so that [com.intellij.openapi.roots.Libraries.Library]
 * created by [org.elixir_lang.DepsWatcher] can be added to the correct [Module].
 */
class Watcher(private val project: Project) : BulkFileListener {
    override fun after(events: MutableList<out VFileEvent>) {
        events.forEach { vFileEvent ->
            when (vFileEvent) {
                is VFileContentChangeEvent -> contentsChanged(vFileEvent)
            }
        }
    }

    private fun contentsChanged(event: VFileContentChangeEvent) {
        if (event.file.name == PackageManager.fileName) {
            ModuleUtil.findModuleForFile(event.file, project)?.let { module ->
                if (event.file.parent == module.moduleFile?.parent) {
                    ProgressManager.getInstance().run(object : Task.Backgroundable(
                            project,
                            "Syncing Libraries for ${module.name} Module",
                            true
                    ) {
                        override fun run(indicator: ProgressIndicator) {
                            syncLibraries(module, indicator)
                        }
                    })
                }
            }
        }
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
            ApplicationManager.getApplication().invokeAndWait {
                ApplicationManager.getApplication().runWriteAction {
                    val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
                    val moduleManager = ModuleManager.getInstance(project)
                    val moduleRootManager = ModuleRootManager.getInstance(module)

                    for (dep in deps) {
                        if (progressIndicator.isCanceled) {
                            break
                        }

                        val depName = dep.application

                        when (dep.type) {
                            Dep.Type.MODULE -> {
                                progressIndicator.text2 = "Adding $depName Module as dependency of ${module.name} Module"

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
                                progressIndicator.text2 = "Adding $depName Library as dependency of ${module.name} Module"

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

private fun Collection<Dep>.externalPaths(project: Project, projectRootManager: ProjectRootManager): Array<VirtualFile> {
    val projectFileIndex = projectRootManager.fileIndex

    return this.mapNotNull { it.externalPath(project, projectFileIndex) }.toTypedArray()
}

private fun Dep.externalPath(project: Project, projectFileIndex: ProjectFileIndex): VirtualFile? =
        virtualFile(project)?.let { virtualFile ->
            if (projectFileIndex.getContentRootForFile(virtualFile) == null &&
                    !projectFileIndex.isInLibrary(virtualFile) &&
                    !projectFileIndex.isExcluded(virtualFile)) {
                virtualFile
            } else {
                null
            }
        }
