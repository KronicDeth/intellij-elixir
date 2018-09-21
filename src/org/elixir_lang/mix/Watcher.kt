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
import org.elixir_lang.PackageManager
import org.elixir_lang.mix.library.Kind
import org.elixir_lang.package_manager.virtualFile

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
) : ModuleComponent, Disposable, VirtualFileListener {
    override fun initComponent() {
        syncLibraries()

        virtualFileManager.addVirtualFileListener(this, this)
    }

    fun syncLibraries() {
        moduleRootManager
                .contentRoots
                .let { recursiveRootsToDepSet(it) }
                .let { syncLibraries(it) }
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        if (event.fileName == org.elixir_lang.mix.PackageManager.fileName && event.file.parent == module.moduleFile?.parent) {
            event
                    .file
                    .let { org.elixir_lang.package_manager.VirtualFile(org.elixir_lang.mix.PackageManager, it) }
                    .let { recursivePackageManagerVirtualFileToDepSet(it) }
                    .let { syncLibraries(it) }
        }
    }

    override fun dispose() = disposeComponent()

    private fun recursiveRootsToDepSet(roots: Array<VirtualFile>): Set<Dep> {
        val packageManagerVirtualFiles = roots.mapNotNull(::virtualFile)

        return recursivePackageManagerVirtualFilesToDepSet(packageManagerVirtualFiles)
    }

    private fun recursivePackageManagerVirtualFilesToDepSet(packageManagerVirtualFiles: List<org.elixir_lang.package_manager.VirtualFile>): Set<Dep> =
            packageManagerVirtualFiles.fold(emptySet()) { acc, packageManagerVirtualFile ->
                acc.union(recursivePackageManagerVirtualFileToDepSet(packageManagerVirtualFile))
            }

    private fun recursivePackageManagerVirtualFileToDepSet(packageManagerVirtualFile: org.elixir_lang.package_manager.VirtualFile): Set<Dep> =
        psiManager.findFile(packageManagerVirtualFile.virtualFile)?.let { recursivePackageManagerPsiFileToDepSet(packageManagerVirtualFile.packageManager, it) } ?: emptySet()

    private fun recursivePackageManagerPsiFileToDepSet(packageManager: PackageManager, psiFile: PsiFile): Set<Dep> {
        val directDepSet = packageManagerPsiFileToDepSet(packageManager, psiFile)

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

    private fun packageManagerPsiFileToDepSet(packageManager: PackageManager, psiFile: PsiFile): Set<Dep> =
        packageManager
                .depGatherer()
                .apply { psiFile.accept(this) }
                .depSet

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
                                moduleRootManager.modifiableModel.run {
                                    addInvalidModuleEntry(depName)
                                    commit()
                                }
                            }
                        }
                        Dep.Type.LIBRARY -> {
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
            }
        }
    }
}
