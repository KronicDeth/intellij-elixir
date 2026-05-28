package org.elixir_lang.sdk.elixir

import com.intellij.facet.FacetManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import org.elixir_lang.Facet

object ElixirSdkLookup {
    fun mostSpecificSdk(module: Module): Sdk? =
        FacetManager.getInstance(module).getFacetByType(Facet.ID)?.sdk
            ?: moduleSdk(module)
            ?: mostSpecificSdk(module.project)

    fun mostSpecificSdk(psiElement: PsiElement): Sdk? {
        val project = psiElement.project
        if (project.isDisposed) return null

        val module = ApplicationManager.getApplication().runReadAction<Module?> {
            ModuleUtilCore.findModuleForPsiElement(psiElement)
        }

        return if (module != null) mostSpecificSdk(module) else mostSpecificSdk(project)
    }

    fun mostSpecificSdk(project: Project): Sdk? = projectSdk(project)

    private fun moduleSdk(module: Module): Sdk? = sdk(ModuleRootManager.getInstance(module).sdk)

    private fun projectSdk(project: Project): Sdk? = sdk(ProjectRootManager.getInstance(project).projectSdk)

    private fun sdk(sdk: Sdk?): Sdk? =
        if (sdk != null && sdk.sdkType === Type.instance) {
            sdk
        } else {
            null
        }
}
