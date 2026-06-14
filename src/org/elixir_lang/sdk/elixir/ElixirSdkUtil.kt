package org.elixir_lang.sdk.elixir

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresReadLock

/**
 * Finds the Elixir SDK for a specific content root by resolving the owning module.
 *
 * Uses [ElixirSdkLookup.mostSpecificSdk] (module overload) which checks Facet SDK → module SDK → project SDK,
 * covering both Rich IDEs (JdkOrderEntry) and Small IDEs (Facet library entry).
 *
 * Falls back to the first module's SDK when no module owns [root] (e.g., external content roots).
 */
@RequiresReadLock
@RequiresBackgroundThread
fun findElixirSdkForRoot(project: Project, root: VirtualFile): Sdk? {
    ThreadingAssertions.assertReadAccess()
    ThreadingAssertions.assertBackgroundThread()
    val module = ModuleUtilCore.findModuleForFile(root, project)
        ?: ModuleManager.getInstance(project).modules.firstOrNull()
        ?: return null
    return ElixirSdkLookup.mostSpecificSdk(module)
}

/**
 * Collects all distinct active Elixir SDKs across all modules in the project.
 *
 * Uses [ElixirSdkLookup.mostSpecificSdk] per module, covering Facet SDKs in Small IDEs.
 * Useful for SDK refresh actions and "does any Elixir SDK exist?" guards.
 */
@RequiresReadLock
fun findAllActiveElixirSdks(project: Project): Set<Sdk> {
    ThreadingAssertions.assertReadAccess()
    val sdks = mutableSetOf<Sdk>()
    for (module in ModuleManager.getInstance(project).modules) {
        ProgressManager.checkCanceled()
        val sdk = ElixirSdkLookup.mostSpecificSdk(module)
        if (sdk != null) sdks.add(sdk)
    }
    return sdks
}
