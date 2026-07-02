package org.elixir_lang.mix.watcher

import com.intellij.openapi.application.readAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager.getCachedValue
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.PackageManager
import org.elixir_lang.mix.Dep
import org.elixir_lang.package_manager.virtualFile
import java.util.*

class Resolution(
    val rootVirtualFileToDepSet: Map<VirtualFile, Set<Dep>>,
    val depToRootVirtualFile: Map<Dep, VirtualFile?>
) {
    companion object {
        /**
         * Resolves the transitive dep-set for all given [rootVirtualFiles].
         *
         * Uses [readAction] (WARA) for PSI access so the EDT is never blocked waiting for a read
         * lock. If a write action preempts a read, the lambda is transparently retried. On retry,
         * the [CachedValue] keyed on each `mix.exs` `PsiFile` avoids reparsing files that haven't
         * changed since the previous attempt, keeping restarts cheap.
         */
        suspend fun resolution(
            psiManager: PsiManager,
            progressIndicator: ProgressIndicator,
            vararg rootVirtualFiles: VirtualFile
        ): Resolution {
            val rootVirtualFileQueue: Queue<VirtualFile> = ArrayDeque<VirtualFile>()
            rootVirtualFileQueue.addAll(rootVirtualFiles)
            val rootVirtualFileToDepSet = mutableMapOf<VirtualFile, Set<Dep>>()
            val depToRootVirtualFile = mutableMapOf<Dep, VirtualFile?>()

            while (rootVirtualFileQueue.isNotEmpty() && !progressIndicator.isCanceled) {
                ProgressManager.checkCanceled()
                val rootVirtualFile = rootVirtualFileQueue.remove()

                if (!rootVirtualFileToDepSet.containsKey(rootVirtualFile)) {
                    val depSet = rootVirtualFileToDepSet(psiManager, progressIndicator, rootVirtualFile)

                    for (dep in depSet) {
                        if (progressIndicator.isCanceled) {
                            break
                        }
                        ProgressManager.checkCanceled()

                        /* Don't need to check module dep because it will already be in `rootVirtualFiles` AND
                           Module deps handle transitivity while Library deps don't */
                        if (dep.type == Dep.Type.LIBRARY) {
                            if (!depToRootVirtualFile.contains(dep)) {
                                val depRootVirtualFile = dep.virtualFile(rootVirtualFile)

                                depToRootVirtualFile[dep] = depRootVirtualFile

                                if (depRootVirtualFile != null &&
                                    !rootVirtualFileToDepSet.contains(depRootVirtualFile) &&
                                    !rootVirtualFileQueue.contains(depRootVirtualFile)
                                ) {
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

        private suspend fun rootVirtualFileToDepSet(
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

        private suspend fun packageVirtualFileToDepSet(
            psiManager: PsiManager,
            progressIndicator: ProgressIndicator,
            packageManager: PackageManager,
            packageVirtualFile: VirtualFile
        ): Set<Dep> {
            // WARA: acquires the read lock without blocking the EDT. If a write action preempts,
            // readAction restarts the lambda. IncorrectOperationException can occur when the
            // VirtualFile is no longer valid (e.g., deleted while we waited for the lock).
            val packagePsiFile = readAction {
                try {
                    psiManager.findFile(packageVirtualFile)
                } catch (@Suppress("unused") error: IncorrectOperationException) {
                    null
                }
            }

            return if (packagePsiFile != null && !progressIndicator.isCanceled) {
                progressIndicator.text2 = "Finding deps in ${packagePsiFile.virtualFile.path}"

                packagePsiFileToDepSet(packageManager, packagePsiFile)
            } else {
                emptySet()
            }
        }

        /**
         * Reads the dep-set from [packagePsiFile] under a WARA read lock.
         *
         * The result is cached via [CachedValueProvider] keyed on [packagePsiFile]'s modification
         * tracker. If a write action cancels the WARA and modifies a *different* file, the cached
         * value for this file remains valid - on restart the cache hit returns the previous result
         * without reparsing. This is correct: only a modification to [packagePsiFile] itself would
         * invalidate the cache and trigger a fresh parse.
         *
         * The [org.elixir_lang.package_manager.DepGatherer] is constructed freshly per WARA attempt (via [PackageManager.depGatherer])
         * so partial results from a cancelled attempt are never reused.
         */
        private suspend fun packagePsiFileToDepSet(
            packageManager: PackageManager,
            packagePsiFile: PsiFile
        ): Set<Dep> =
            readAction {
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
