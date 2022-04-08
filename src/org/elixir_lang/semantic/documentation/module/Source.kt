package org.elixir_lang.semantic.documentation.module

import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.semantic.documentation.Module
import org.elixir_lang.semantic.module_attribute.definition.ModuleAttribute

class Source(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
    ModuleAttribute(atUnqualifiedNoParenthesesCall), Module {
    override val text: String
        get() = TODO("Not yet implemented")
}
