package org.elixir_lang.mix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

/**
 * Watches the [module]'s `mix.exs` for changes to the `deps`, so that [com.intellij.openapi.roots.Libraries.Library]
 * created by [org.elixir_lang.DepsWatcher] can be added to the correct [Module].
 */
class Watcher(
        private val module: Module,
        private val project: Project,
        private val moduleManager: ModuleManager,
        private val moduleRootManager: ModuleRootManager,
        private val psiManager: PsiManager,
        private val virtualFileManager: VirtualFileManager
) :
        ModuleComponent, Disposable, VirtualFileListener {
    override fun initComponent() {
        moduleRootManager
                .contentRoots
                .let { recursiveRootsToDepSet(it) }
                .let { syncLibraries(it) }

        virtualFileManager.addVirtualFileListener(this, this)
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        if (event.fileName == "mix.exs" && event.file.parent == module.moduleFile?.parent) {
            recursiveMixFileToDepSet(event.file)
                    .let { syncLibraries(it) }
        }
    }

    override fun dispose() = disposeComponent()

    private fun recursiveRootsToDepSet(roots: Array<VirtualFile>): Set<Dep> {
        val mixFiles = rootsToMixFiles(roots)

        return recursiveMixFilesToDepSet(mixFiles)
    }

    private fun rootsToMixFiles(roots: Array<VirtualFile>) = roots.mapNotNull { it.findChild("mix.exs") }

    private fun recursiveMixFilesToDepSet(mixFiles: List<VirtualFile>): Set<Dep> =
            mixFiles.fold(emptySet()) { acc, mixFile ->
                acc.union(recursiveMixFileToDepSet(mixFile))
            }

    private fun recursiveMixFileToDepSet(mixFile: VirtualFile): Set<Dep> =
        psiManager.findFile(mixFile)?.let { recursiveMixFileToDepSet(it) } ?: emptySet()

    private fun recursiveMixFileToDepSet(mixFile: PsiFile): Set<Dep> {
        val directDepSet = mixFileToDepSet(mixFile)

        return directDepSet.fold(directDepSet) { acc, dep ->
            val depDepSet = if (dep.type == Dep.Type.LIBRARY) {
                project.baseDir.findFileByRelativePath(dep.path)?.let { root ->
                    recursiveRootsToDepSet(arrayOf(root))
                }
            } else {
                null
            } ?: emptySet()

            acc.union(depDepSet)
        }
    }

    private fun mixFileToDepSet(mixFile: PsiFile): Set<Dep> {
        val depGatherer = DepGatherer()
        mixFile.accept(depGatherer)

        return depGatherer.depSet
    }

    private fun syncLibraries(deps: Collection<Dep>) {
        ApplicationManager.getApplication().invokeLater {
            ApplicationManager.getApplication().runWriteAction {
                val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
                val missingDeps = mutableListOf<Dep>()

                for (dep in deps) {
                    val depName = dep.application

                    when (dep.type) {
                        Dep.Type.MODULE -> {
                            val depModule = moduleManager.findModuleByName(depName)

                            if (depModule != null) {
                                if (!moduleRootManager.isDependsOn(depModule)) {
                                    ModuleRootModificationUtil.addDependency(module, depModule)
                                }
                            } else {
                                missingDeps.add(dep)
                            }
                        }
                        Dep.Type.LIBRARY -> {
                            val depLibrary = libraryTable.getLibraryByName(depName)

                            if (depLibrary != null) {
                                if (moduleRootManager.orderEntries.none { it is LibraryOrderEntry && it.libraryName == depName }) {
                                    ModuleRootModificationUtil.addDependency(module, depLibrary)
                                }
                            } else {
                                missingDeps.add(dep)
                            }
                        }
                    }
                }

                assert(missingDeps.size >= 0)
            }
        }
    }
}
