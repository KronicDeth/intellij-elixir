package org.elixir_lang.sdk.elixir

import com.intellij.facet.FacetManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.Facet

/**
 * Resolves the most specific Elixir SDK for a given [Module], [Project], or [PsiElement].
 *
 * All overloads require a read lock ([RequiresReadLock]). Callers that are not already inside
 * a platform-provided read action (e.g. formatting service callbacks) must acquire one
 * explicitly via [com.intellij.openapi.application.ReadAction.nonBlocking].
 *
 * Resolution order for [Module]: Facet SDK → module SDK → project SDK.
 */
object ElixirSdkLookup {
    private val LOG = com.intellij.openapi.diagnostic.logger<ElixirSdkLookup>()

    @RequiresReadLock
    fun mostSpecificSdk(module: Module): Sdk? {
        ThreadingAssertions.assertReadAccess()
        val facetSdk = FacetManager.getInstance(module).getFacetByType(Facet.ID)?.sdk
        if (facetSdk != null) {
            LOG.trace("ElixirSdkLookup.mostSpecificSdk(module='${module.name}'): resolved from Facet → '${facetSdk.name}'")
            return facetSdk
        }

        val modSdk = moduleSdk(module)
        if (modSdk != null) {
            LOG.trace("ElixirSdkLookup.mostSpecificSdk(module='${module.name}'): resolved from ModuleRootManager → '${modSdk.name}'")
            return modSdk
        }

        val projSdk = mostSpecificSdk(module.project)
        LOG.trace("ElixirSdkLookup.mostSpecificSdk(module='${module.name}'): fell through to project SDK → '${projSdk?.name}'")
        return projSdk
    }

    @RequiresReadLock
    fun mostSpecificSdk(psiElement: PsiElement): Sdk? {
        ThreadingAssertions.assertReadAccess()
        val project = psiElement.project
        if (project.isDisposed) return null

        val module = ModuleUtilCore.findModuleForPsiElement(psiElement)

        return if (module != null) mostSpecificSdk(module) else mostSpecificSdk(project)
    }

    @RequiresReadLock
    fun mostSpecificSdk(project: Project): Sdk? {
        ThreadingAssertions.assertReadAccess()
        return projectSdk(project)
    }

    private fun moduleSdk(module: Module): Sdk? {
        val raw = ModuleRootManager.getInstance(module).sdk
        val filtered = sdk(raw)
        if (raw != null && filtered == null) {
            LOG.trace("ElixirSdkLookup.moduleSdk(module='${module.name}'): raw SDK '${raw.name}' (type=${raw.sdkType.name}) rejected by type filter")
        }
        return filtered
    }

    private fun projectSdk(project: Project): Sdk? = sdk(ProjectRootManager.getInstance(project).projectSdk)

    private fun sdk(sdk: Sdk?): Sdk? =
        if (sdk != null && sdk.sdkType === Type.instance) {
            sdk
        } else {
            null
        }
}
