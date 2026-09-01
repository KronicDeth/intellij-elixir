package org.elixir_lang.mix.watcher

import com.intellij.openapi.application.readAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VfsUtilCore
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
            // Anything reached that was not handed in got here by following a Dep.Type.LIBRARY dep,
            // so Mix would resolve its deps as a dependency's. Membership rather than dequeue order
            // decides it, so a root reads the same however the queue is drained. Umbrella apps are
            // handed in by the caller alongside the roots, and MODULE deps are never walked, so
            // neither can be mistaken for a dependency.
            val projectRootVirtualFiles = rootVirtualFiles.toHashSet()
            val rootVirtualFileToDepSet = mutableMapOf<VirtualFile, Set<Dep>>()
            val depToRootVirtualFile = mutableMapOf<Dep, VirtualFile?>()

            while (rootVirtualFileQueue.isNotEmpty() && !progressIndicator.isCanceled) {
                ProgressManager.checkCanceled()
                val rootVirtualFile = rootVirtualFileQueue.remove()

                if (!rootVirtualFileToDepSet.containsKey(rootVirtualFile)) {
                    val depSet = rootVirtualFileToDepSet(
                        psiManager,
                        progressIndicator,
                        rootVirtualFile,
                        isDependency = rootVirtualFile !in projectRootVirtualFiles,
                    )

                    for (dep in depSet) {
                        if (progressIndicator.isCanceled) {
                            break
                        }
                        ProgressManager.checkCanceled()

                        /* Don't need to check module dep because it will already be in `rootVirtualFiles` AND
                           Module deps handle transitivity while Library deps don't */
                        if (dep.type == Dep.Type.LIBRARY) {
                            if (!depToRootVirtualFile.contains(dep)) {
                                val depRootVirtualFile =
                                    depRootVirtualFile(dep, rootVirtualFile, projectRootVirtualFiles)

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

        /**
         * The directory Mix would check [dep] out into.
         *
         * `Mix.Project.deps_config/1` hands every dependency the *top-level* project's `deps_path`,
         * already expanded, so a hex or git dep declared by a dependency is checked out beside it
         * under the project - `deps/<parent>/deps/<child>` is a directory no Mix configuration
         * produces. Looking there ended the walk one level below every project root.
         *
         * An explicit `path:` dep is the exception and keeps the old base: `Mix.SCM.Path` expands it
         * against the directory of whatever declared it, so `"../sibling"` inside a dependency
         * really is relative to that dependency.
         *
         * The search is confined to the one project that owns the declaring file, so two content
         * roots that both have `deps/<name>` cannot be confused for one another. The lookup stays on
         * [VirtualFile.findFileByRelativePath] because a `deps/` path needs no upward traversal,
         * leaving [Dep.virtualFile]'s refresh-based resolution for the paths that do.
         */
        private fun depRootVirtualFile(
            dep: Dep,
            declaringRootVirtualFile: VirtualFile,
            projectRootVirtualFiles: Set<VirtualFile>
        ): VirtualFile? {
            val owningProjectRoot = if (dep.path.startsWith(MIX_DEPS_DIRECTORY_PREFIX)) {
                owningProjectRoot(declaringRootVirtualFile, projectRootVirtualFiles)
            } else {
                null
            }

            return owningProjectRoot?.findFileByRelativePath(dep.path)
                ?: dep.virtualFile(declaringRootVirtualFile)
        }

        /**
         * The content root of the Mix project whose `deps` directory Mix would install into.
         *
         * That is the innermost root containing [declaringRootVirtualFile], except for an umbrella
         * app: an app shares the umbrella's `deps`, `_build` and lock file and never has its own, so
         * a `deps` directory beside its `mix.exs` is debris from before it joined the umbrella.
         * Every other content root is a project in its own right and keeps its own `deps`, including
         * one that merely happens to sit inside another.
         */
        private fun owningProjectRoot(
            declaringRootVirtualFile: VirtualFile,
            projectRootVirtualFiles: Set<VirtualFile>
        ): VirtualFile? {
            var root = projectRootVirtualFiles
                .filter { VfsUtilCore.isAncestor(it, declaringRootVirtualFile, false) }
                .maxByOrNull { it.path.length }
                ?: return null

            while (true) {
                val umbrella = root.parent
                    ?.takeIf { it.name == UMBRELLA_APPS_DIRECTORY_NAME }
                    ?.parent
                    ?.takeIf { it in projectRootVirtualFiles }
                    ?: return root

                root = umbrella
            }
        }

        private suspend fun rootVirtualFileToDepSet(
            psiManager: PsiManager,
            progressIndicator: ProgressIndicator,
            rootVirtualFile: VirtualFile,
            isDependency: Boolean
        ): Set<Dep> {
            progressIndicator.text2 = "Finding package file under ${rootVirtualFile.path}"
            val packageManagerVirtualFile = virtualFile(rootVirtualFile)

            return if (packageManagerVirtualFile != null) {
                val (packageManager, packageVirtualFile) = packageManagerVirtualFile

                packageVirtualFileToDepSet(
                    psiManager,
                    progressIndicator,
                    packageManager,
                    packageVirtualFile,
                    isDependency
                )
            } else {
                emptySet()
            }
        }

        private suspend fun packageVirtualFileToDepSet(
            psiManager: PsiManager,
            progressIndicator: ProgressIndicator,
            packageManager: PackageManager,
            packageVirtualFile: VirtualFile,
            isDependency: Boolean
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

                packagePsiFileToDepSet(packageManager, packagePsiFile, isDependency)
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
            packagePsiFile: PsiFile,
            isDependency: Boolean
        ): Set<Dep> =
            readAction {
                // Two keys, because the dep set a file yields now depends on how the file was
                // reached, while a CachedValue outlives the resolution that populated it: one
                // module's run can hand in a root that another's reaches as a dependency.
                getCachedValue(packagePsiFile, if (isDependency) DEPENDENCY_DEP_SET else PROJECT_DEP_SET) {
                    packageManager
                        .depGatherer(isDependency)
                        .apply { packagePsiFile.accept(this) }
                        .depSet.toSet()
                        .let { CachedValueProvider.Result.create(it, packagePsiFile) }
                }
            }
    }
}

private const val MIX_DEPS_DIRECTORY_PREFIX = "deps/"
private const val UMBRELLA_APPS_DIRECTORY_NAME = "apps"

private val PROJECT_DEP_SET: Key<CachedValue<Set<Dep>>> = Key.create<CachedValue<Set<Dep>>>("PROJECT_DEP_SET")
private val DEPENDENCY_DEP_SET: Key<CachedValue<Set<Dep>>> = Key.create<CachedValue<Set<Dep>>>("DEPENDENCY_DEP_SET")
