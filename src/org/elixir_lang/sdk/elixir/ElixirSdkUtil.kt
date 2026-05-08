package org.elixir_lang.sdk.elixir

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.VirtualFile

/**
 * Finds the Elixir SDK for a specific content root by resolving the owning module.
 *
 * Uses [Type.mostSpecificSdk] (module overload) which checks Facet SDK → module SDK → project SDK,
 * covering both Rich IDEs (JdkOrderEntry) and Small IDEs (Facet library entry).
 *
 * Falls back to the first module's SDK when no module owns [root] (e.g., external content roots).
 */
fun findElixirSdkForRoot(project: Project, root: VirtualFile): Sdk? {
    val module = ModuleUtilCore.findModuleForFile(root, project)
        ?: ModuleManager.getInstance(project).modules.firstOrNull()
        ?: return null
    return Type.mostSpecificSdk(module)
}

/**
 * Collects all distinct active Elixir SDKs across all modules in the project.
 *
 * Uses [Type.mostSpecificSdk] per module, covering Facet SDKs in Small IDEs.
 * Useful for SDK refresh actions and "does any Elixir SDK exist?" guards.
 */
fun findAllActiveElixirSdks(project: Project): Set<Sdk> {
    val sdks = mutableSetOf<Sdk>()
    for (module in ModuleManager.getInstance(project).modules) {
        val sdk = Type.mostSpecificSdk(module)
        if (sdk != null) sdks.add(sdk)
    }
    return sdks
}
