package org.elixir_lang.semantic.documentation

import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.semantic.Documentation
import org.elixir_lang.semantic.module_attribute.definition.ModuleAttribute

class CallDefinition(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
    ModuleAttribute(atUnqualifiedNoParenthesesCall), Documentation {
    override val text: String
        get() = TODO("Not yet implemented")
}
