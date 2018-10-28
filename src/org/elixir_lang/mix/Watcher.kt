package org.elixir_lang.mix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
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
        virtualFileManager.addVirtualFileListener(this, this)
    }

    fun syncLibraries(progressIndicator: ProgressIndicator) {
        moduleRootManager
                .contentRoots
                .let { recursiveRootsToDepSet(it, emptySet(), progressIndicator) }
                .let { syncLibraries(it, progressIndicator) }
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        if (event.fileName == org.elixir_lang.mix.PackageManager.fileName && event.file.parent == module.moduleFile?.parent) {
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Libraries for $module.name Module", true) {
                override fun run(indicator: ProgressIndicator) {
                    event
                    .file
                    .let { org.elixir_lang.package_manager.VirtualFile(org.elixir_lang.mix.PackageManager, it) }
                    .let { recursivePackageManagerVirtualFileToDepSet(it, emptySet(), indicator) }
                    .let { syncLibraries(it, indicator) }
                }
            })

        }
    }

    override fun dispose() = disposeComponent()

    private fun recursiveRootsToDepSet(roots: Array<VirtualFile>, acc: Set<Dep>, progressIndicator: ProgressIndicator): Set<Dep> {
        val packageManagerVirtualFiles = roots.mapNotNull(::virtualFile)

        return recursivePackageManagerVirtualFilesToDepSet(packageManagerVirtualFiles, acc, progressIndicator)
    }

    private fun recursivePackageManagerVirtualFilesToDepSet(
            packageManagerVirtualFiles: List<org.elixir_lang.package_manager.VirtualFile>,
            initial: Set<Dep>,
            progressIndicator: ProgressIndicator
    ): Set<Dep> =
            packageManagerVirtualFiles.fold(initial) { acc, packageManagerVirtualFile ->
                acc.union(recursivePackageManagerVirtualFileToDepSet(packageManagerVirtualFile, acc, progressIndicator))
            }

    private fun recursivePackageManagerVirtualFileToDepSet(
            packageManagerVirtualFile: org.elixir_lang.package_manager.VirtualFile,
            initial: Set<Dep>,
            progressIndicator: ProgressIndicator
    ): Set<Dep> {
        val psiFile = runReadAction {
            psiManager.findFile(packageManagerVirtualFile.virtualFile)
        }

        return psiFile?.let { recursivePackageManagerPsiFileToDepSet(packageManagerVirtualFile.packageManager, it, initial, progressIndicator) } ?: initial
    }

    private fun recursivePackageManagerPsiFileToDepSet(
            packageManager: PackageManager,
            psiFile: PsiFile,
            initial: Set<Dep>,
            progressIndicator: ProgressIndicator
    ): Set<Dep> =
        packageManager
                .let {
                    packageManagerPsiFileToDepSet(it, psiFile, progressIndicator)
                }
                .asSequence()
                .filterNot { it in initial }
                .filter { it.type == Dep.Type.LIBRARY }
                .mapNotNull { dep ->
                    project.baseDir.findFileByRelativePath(dep.path)?.let { root ->
                        recursiveRootsToDepSet(arrayOf(root), initial.union(setOf(dep)), progressIndicator)
                    }
                }
                .fold(initial) { acc, depDepSet ->
                    acc.union(depDepSet)
                }

    private fun packageManagerPsiFileToDepSet(packageManager: PackageManager, psiFile: PsiFile, progressIndicator: ProgressIndicator): Set<Dep> {
        progressIndicator.text2 = "Gathering dependency set from ${psiFile.virtualFile.path}"

        return if (!progressIndicator.isCanceled) {
            packageManager
                    .depGatherer()
                    .apply { psiFile.accept(this) }
                    .depSet
        } else {
            emptySet()
        }
    }

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
                }
            }
        }
    }
}
