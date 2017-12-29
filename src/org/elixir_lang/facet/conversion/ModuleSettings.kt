package org.elixir_lang.facet.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.ModuleSettings
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import org.elixir_lang.facet.SdksService
import org.elixir_lang.facet.configurable.Project.Companion.FACET_NAME
import org.elixir_lang.sdk.elixir.ForSmallIdes
import org.elixir_lang.sdk.elixir.Type
import org.jdom.Element

class ModuleSettings(private val conversionContext: ConversionContext) : ConversionProcessor<ModuleSettings>() {
    override fun isConversionNeeded(moduleSettings: ModuleSettings): Boolean = element(moduleSettings) != null

    override fun process(moduleSettings: ModuleSettings) {
        element(moduleSettings)?.let {
            it.detach()

            val classRoots = conversionContext.getLibraryClassRoots(
                    ForSmallIdes.LIBRARY_NAME,
                    LibraryTablesRegistrar.PROJECT_LEVEL
            )

            if (classRoots.size == 1) {
                classRoots.forEach { classRoot ->
                    if (moduleSettings.getFacetElement(org.elixir_lang.facet.Type.ID) == null) {
                        val projectSdksModel = SdksService.getInstance()!!.getModel()
                        projectSdksModel.addSdk(Type.getInstance(), classRoot.path, { sdk ->
                            moduleSettings.addFacetElement(
                                    org.elixir_lang.facet.Type.ID,
                                    FACET_NAME,
                                    Element("configuration").apply { setAttribute("sdkName", sdk.name) }
                            )
                        })
                        projectSdksModel.apply()
                    }
                }
            }
        }
    }

    private fun element(moduleSettings: ModuleSettings): Element? =
            moduleSettings.orderEntries.find {
                it.getAttributeValue("type") == "library" &&
                        it.getAttributeValue("name") == ForSmallIdes.LIBRARY_NAME
            }
}
