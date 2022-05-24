package org.elixir_lang.facet

import com.intellij.facet.FacetType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import org.elixir_lang.Facet
import org.elixir_lang.Icons
import org.elixir_lang.module.ElixirModuleType
import javax.swing.Icon

class Type : FacetType<Facet, Configuration>(Facet.ID, ID, "Elixir") {
    override fun createDefaultConfiguration(): Configuration = Configuration()

    override fun createFacet(
        module: Module,
        name: String,
        configuration: Configuration,
        underlyingFacet: com.intellij.facet.Facet<*>?
    ): Facet = Facet(this, module, name, configuration, underlyingFacet)

    override fun isSuitableModuleType(moduleType: ModuleType<*>): Boolean =
        moduleType.id != ElixirModuleType.MODULE_TYPE_ID

    override fun getIcon(): Icon = Icons.LANGUAGE

    companion object {
        internal const val ID = "Elixir"
    }
}
