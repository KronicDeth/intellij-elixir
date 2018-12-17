package org.elixir_lang.mix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiManager
import org.elixir_lang.DepsWatcher
import org.elixir_lang.mix.library.Kind
import org.elixir_lang.mix.watcher.TransitiveResolution.transitiveResolution

/**
 * Watches the [module]'s `mix.exs` for changes to the `deps`, so that [com.intellij.openapi.roots.Libraries.Library]
 * created by [org.elixir_lang.DepsWatcher] can be added to the correct [Module].
 */
class Watcher(
        private val module: Module,
        private val project: Project,
        private val projectRootManager: ProjectRootManager,
        private val moduleManager: ModuleManager,
        private val moduleRootManager: ModuleRootManager,
        private val psiManager: PsiManager,
        private val virtualFileManager: VirtualFileManager
) : ModuleComponent, Disposable, VirtualFileListener {
    override fun initComponent() {
        virtualFileManager.addVirtualFileListener(this, this)
    }

    fun syncLibraries(progressIndicator: ProgressIndicator) {
        moduleRootManager
                .contentRoots
                .let { transitiveResolution(project, psiManager, progressIndicator, *it) }
                .let { syncLibraries(it, progressIndicator) }
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        if (event.fileName == org.elixir_lang.mix.PackageManager.fileName &&
                event.file.parent == module.moduleFile?.parent) {
            ProgressManager.getInstance().run(object : Task.Backgroundable(
                    project,
                    "Syncing Libraries for ${module.name} Module",
                    true
            ) {
                override fun run(indicator: ProgressIndicator) {
                    syncLibraries(indicator)
                }
            })

        }
    }

    override fun dispose() = disposeComponent()

    private fun syncLibraries(deps: Collection<Dep>, progressIndicator: ProgressIndicator) {
        if (deps.isNotEmpty()) {
            ApplicationManager.getApplication().invokeAndWait {
                ApplicationManager.getApplication().runWriteAction {
                    val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

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
        val externalPaths = deps.externalPaths(project, projectRootManager)

        if (externalPaths.isNotEmpty()) {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

            project.getComponent(DepsWatcher::class.java).syncLibraries(externalPaths, libraryTable, progressIndicator)
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
                !projectFileIndex.isInLibrary(virtualFile)  &&
                !projectFileIndex.isExcluded(virtualFile)) {
            virtualFile
        } else {
            null
        }
    }
