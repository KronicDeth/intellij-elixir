package org.elixir_lang.facet

import com.intellij.facet.FacetType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.ProjectJdkTable
import org.elixir_lang.Facet
import org.elixir_lang.icons.ElixirIcons
import org.elixir_lang.module.ElixirModuleType
import javax.swing.Icon

class Type : FacetType<Facet, Configuration>(Facet.ID, ID, "Elixir") {
    override fun createDefaultConfiguration(): Configuration {
        val defaultConfiguration = Configuration()
        val sdkList = ProjectJdkTable.getInstance().getSdksOfType(org.elixir_lang.sdk.elixir.Type.getInstance())

        if (sdkList.size > 0) {
            defaultConfiguration.sdk = sdkList[0]
        }

        return defaultConfiguration
    }

    override fun createFacet(module: Module,
                             name: String,
                             configuration: Configuration,
                             underlyingFacet: com.intellij.facet.Facet<*>?): Facet {
        return Facet(this, module, name, configuration, underlyingFacet)
    }

    override fun isSuitableModuleType(moduleType: ModuleType<*>): Boolean {
        return moduleType.id != ElixirModuleType.MODULE_TYPE_ID
    }

    override fun getIcon(): Icon {
        return ElixirIcons.FILE
    }

    companion object {
        internal val ID = "Elixir"
    }
}
