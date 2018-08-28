package org.elixir_lang.facet.external_library.conversion

import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.ModuleSettings
import com.intellij.conversion.ModuleSettings.MODULE_ROOT_MANAGER_COMPONENT
import org.elixir_lang.facet.Type
import org.jdom.Element

class ModuleSettings : ConversionProcessor<ModuleSettings>() {
    override fun isConversionNeeded(moduleSettings: ModuleSettings): Boolean {
        val sdkName = moduleSettings.sdkName()

        return sdkName != null && sdkName.isNotBlank() && moduleSettings.applicationLibrary(sdkName) == null
    }

    override fun process(moduleSettings: ModuleSettings) {
        moduleSettings.sdkName()?.let { sdkName ->
            moduleSettings.getComponentElement(MODULE_ROOT_MANAGER_COMPONENT)?.let { moduleRootManagerComponentElement ->
                val orderEntryElement = Element("orderEntry")
                orderEntryElement.setAttribute("type", "library")
                orderEntryElement.setAttribute("name", sdkName)
                orderEntryElement.setAttribute("level", "application")
                moduleRootManagerComponentElement.addContent(orderEntryElement)
            }
        }
    }
}

private fun ModuleSettings.sdkName(): String? =
        getFacetElement(Type.ID)?.let { facetElement ->
            facetElement
                    .getChildren("configuration")
                    .mapNotNull { it.getAttributeValue("sdkName") }
                    .singleOrNull()
        }

private fun ModuleSettings.applicationLibrary(name: String): Element? =
        orderEntries.find {
            it.getAttributeValue("type") == "library" && it.getAttributeValue("level") == "application" && it.getAttributeValue("name") == name
        }
