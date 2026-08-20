package org.elixir_lang.mix.watcher

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.elixir_lang.mix.Dep
import java.util.*

object TransitiveResolution {
    /**
     * Computes the full set of transitive deps reachable from [rootVirtualFiles].
     *
     * Delegates PSI reads to [Resolution.resolution] which uses WARA ([com.intellij.openapi.application.readAction]) internally,
     * allowing write actions to preempt without blocking the EDT.
     */
    suspend fun transitiveResolution(
            psiManager: PsiManager,
            progressIndicator: ProgressIndicator,
            vararg rootVirtualFiles: VirtualFile
    ): Set<Dep> =
            transitiveResolution(
                Resolution.resolution(psiManager, progressIndicator, *rootVirtualFiles),
                *rootVirtualFiles
            )

    // Non-suspend: walks pre-computed maps with no PSI access.
    private fun transitiveResolution(resolution: Resolution, vararg rootVirtualFiles: VirtualFile): Set<Dep> {
        val visitedDepSet = mutableSetOf<Dep>()
        val unvisitedDepQueue = ArrayDeque<Dep>()

        for (rootVirtualFile in rootVirtualFiles) {
            val rootVirtualFileDepSet = resolution.rootVirtualFileToDepSet[rootVirtualFile]!!
            unvisitedDepQueue.addAll(rootVirtualFileDepSet)
        }

        while (unvisitedDepQueue.isNotEmpty()) {
            val unvisitedDep =  unvisitedDepQueue.remove()

            resolution.depToRootVirtualFile[unvisitedDep]?.let { depRootVirtualFile ->
                for (depDep in resolution.rootVirtualFileToDepSet[depRootVirtualFile]!!) {
                    if (!visitedDepSet.contains(depDep)) {
                        unvisitedDepQueue.add(depDep)
                    }
                }
            }

            visitedDepSet.add(unvisitedDep)
        }

        return visitedDepSet
    }
}
