package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewTypeLocation

interface Use : Semantic {
    val modulars: Set<Modular>
    val moduleAttributes: kotlin.collections.List<org.elixir_lang.semantic.module_attribute.definition.Literal>
    val types: kotlin.collections.List<org.elixir_lang.semantic.type.Definition>
    val exportedCallDefinitions:
            kotlin.collections.List<org.elixir_lang.semantic.call.Definition>
    val callDefinitions:
            kotlin.collections.List<org.elixir_lang.semantic.call.Definition>
    val variables: kotlin.collections.List<Variable>

    override fun elementDescription(location: ElementDescriptionLocation): String? = when (location) {
        UsageViewTypeLocation.INSTANCE -> "use"
        else -> null
    }
}
