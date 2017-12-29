package org.elixir_lang.facet.configurable

import com.intellij.application.options.ModuleAwareProjectConfigurable
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.Facet
import org.elixir_lang.facet.Configurable
import org.elixir_lang.facet.Type

class Project(project: Project) : ModuleAwareProjectConfigurable<Configurable>(project, "Elixir", null) {
    override fun isSuitableForModule(module: Module): Boolean {
        @Suppress("DEPRECATION")
        return !module.getOptionValue(Module.ELEMENT_TYPE).equals("ELIXIR_MODULE")
    }

    override fun createModuleConfigurable(module: com.intellij.openapi.module.Module): Configurable {
        return object : Configurable(module) {
            override fun applySdk(sdk: Sdk?) {
                val facetManager = FacetManager.getInstance(module)
                val facet = facetManager.getFacetByType(Facet.ID)

                if (facet == null) {
                    ApplicationManager.getApplication().runWriteAction {
                        addFacet(facetManager, sdk)
                    }
                } else {
                    setFacetSdk(facet, sdk)
                }
            }

            override fun initSdk(): Sdk? = FacetManager.getInstance(module).getFacetByType(Facet.ID)?.configuration?.sdk
        }
    }

    private fun setFacetSdk(facet: Facet, sdk: Sdk?) {
        facet.apply {
            configuration.sdk = sdk

            module.apply {
                if (!isDisposed) {
                    messageBus.syncPublisher(FacetManager.FACETS_TOPIC).facetConfigurationChanged(facet)
                }
            }
        }
    }

    private fun addFacet(facetManager: FacetManager, sdk: Sdk?) {
        val facet = facetManager.addFacet(FacetType.findInstance(Type::class.java), FACET_NAME, null)
        setFacetSdk(facet, sdk)
    }

    companion object {
        const val FACET_NAME = "Elixir facet"
    }
}
