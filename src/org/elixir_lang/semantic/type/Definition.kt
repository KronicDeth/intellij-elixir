package org.elixir_lang.semantic.type

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArity
import org.elixir_lang.semantic.Named
import org.elixir_lang.semantic.type.definition.Body
import org.elixir_lang.semantic.type.definition.Guard
import org.elixir_lang.semantic.type.definition.Head

interface Definition : Named {
    override val name: String
        get() = nameArity.name
    val nameArity: NameArity
    val head: Head?
    val body: Body?
    val guard: Guard?

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> "type"
            else -> null
        }
}
