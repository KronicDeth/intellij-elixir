package org.elixir_lang.facet.configurable

import com.intellij.application.options.ModuleAwareProjectConfigurable
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Facet
import org.elixir_lang.facet.Configurable
import org.elixir_lang.facet.Type

class Project(project: Project) : ModuleAwareProjectConfigurable<Configurable>(project, "Elixir", null) {
    override fun isSuitableForModule(module: Module): Boolean =
    // CANNOT use ModuleType.is(module, ElixirModuleType.getInstance()) as ElixirModuleType depends on
        // JavaModuleBuilder and so only available in IntelliJ
        ModuleType.get(module).id != "ELIXIR_MODULE"

    override fun createModuleConfigurable(module: Module): Configurable {
        return object : Configurable(module) {
            override fun applySdk(sdk: Sdk?) {
                val facetManager = FacetManager.getInstance(module)

                ApplicationManager.getApplication().runWriteAction {
                    val facet = facetManager.getFacetByType(Facet.ID) ?: addFacet(facetManager)
                    facet.sdk = sdk
                }
            }

            override fun initSdk(): Sdk? = Facet.sdk(module)
        }
    }

    private fun addFacet(facetManager: FacetManager): Facet =
        facetManager.addFacet(FacetType.findInstance(Type::class.java), FACET_NAME, null)

    companion object {
        const val FACET_NAME = "Elixir facet"
    }
}
