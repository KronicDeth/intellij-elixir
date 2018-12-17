package org.elixir_lang.mix.watcher

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import org.elixir_lang.mix.Dep
import org.elixir_lang.PackageManager
import org.elixir_lang.package_manager.virtualFile
import java.util.*

class Resolution(
        val rootVirtualFileToDepSet: Map<VirtualFile, Set<Dep>>,
        val depToRootVirtualFile: Map<Dep, VirtualFile?>
) {
    companion object {
        fun resolution(
                project: Project,
                psiManager: PsiManager,
                progressIndicator: ProgressIndicator,
                vararg rootVirtualFiles: VirtualFile
        ): Resolution {
            val rootVirtualFileQueue: Queue<VirtualFile> = ArrayDeque<VirtualFile>()
            rootVirtualFileQueue.addAll(rootVirtualFiles)
            val rootVirtualFileToDepSet = mutableMapOf<VirtualFile, Set<Dep>>()
            val depToRootVirtualFile = mutableMapOf<Dep, VirtualFile?>()

            while (rootVirtualFileQueue.isNotEmpty() && !progressIndicator.isCanceled) {
                val rootVirtualFile = rootVirtualFileQueue.remove()

                if (!rootVirtualFileToDepSet.containsKey(rootVirtualFile)) {
                    val depSet = rootVirtualFileToDepSet(psiManager, progressIndicator, rootVirtualFile)

                    for (dep in depSet) {
                        if (progressIndicator.isCanceled) {
                            break
                        }

                        /* Don't need to check module dep because it will already be in `rootVirtualFiles` AND
                           Module deps handle transitivity while Library deps don't */
                        if (dep.type == Dep.Type.LIBRARY) {
                            if (!depToRootVirtualFile.contains(dep)) {
                                val depRootVirtualFile = dep.virtualFile(project)

                                depToRootVirtualFile[dep] = depRootVirtualFile

                                if (depRootVirtualFile != null &&
                                        !rootVirtualFileToDepSet.contains(depRootVirtualFile) &&
                                        !rootVirtualFileQueue.contains(depRootVirtualFile)) {
                                    rootVirtualFileQueue.add(depRootVirtualFile)
                                }
                            }
                        }
                    }

                    rootVirtualFileToDepSet[rootVirtualFile] = depSet
                }
            }

            return Resolution(rootVirtualFileToDepSet, depToRootVirtualFile)
        }

        private fun rootVirtualFileToDepSet(
                psiManager: PsiManager,
                progressIndicator: ProgressIndicator,
                rootVirtualFile: VirtualFile
        ): Set<Dep> {
            progressIndicator.text2 = "Finding package file under ${rootVirtualFile.path}"
            val packageManagerVirtualFile = virtualFile(rootVirtualFile)

            return if (packageManagerVirtualFile != null) {
                val (packageManager, packageVirtualFile) = packageManagerVirtualFile

                packageVirtualFileToDepSet(psiManager, progressIndicator, packageManager, packageVirtualFile)
            } else {
                emptySet()
            }
        }

        private fun packageVirtualFileToDepSet(
                psiManager: PsiManager,
                progressIndicator: ProgressIndicator,
                packageManager: PackageManager,
                packageVirtualFile: VirtualFile
        ): Set<Dep> {
            val packagePsiFile = runReadAction { psiManager.findFile(packageVirtualFile) }

            return if (packagePsiFile != null && !progressIndicator.isCanceled) {
                progressIndicator.text2 = "Finding deps in ${packagePsiFile.virtualFile.path}"

                packagePsiFileToDepSet(packageManager, packagePsiFile)
            } else {
                emptySet()
            }
        }

        private fun packagePsiFileToDepSet(
                packageManager: PackageManager,
                packagePsiFile: PsiFile
        ): Set<Dep> =
                runReadAction {
                    getCachedValue(packagePsiFile, DEP_SET) {
                        packageManager
                                .depGatherer()
                                .apply { packagePsiFile.accept(this) }
                                .depSet.toSet()
                                .let { CachedValueProvider.Result.create(it, packagePsiFile) }
                    }
                }
    }
}

private val DEP_SET: Key<CachedValue<Set<Dep>>> = Key.create<CachedValue<Set<Dep>>>("DEP_SET")
