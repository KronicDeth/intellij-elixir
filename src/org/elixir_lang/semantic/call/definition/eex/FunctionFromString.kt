package org.elixir_lang.semantic.call.definition.eex

import com.intellij.psi.ElementDescriptionLocation
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Modular

class FunctionFromString(enclosingModular: Modular, call: Call) : FunctionFrom(enclosingModular, call) {
    override val presentation: NameArityInterval
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        const val FUNCTION = "function_from_string"
    }
}
