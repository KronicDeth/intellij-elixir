package org.elixir_lang.semantic.type.definition.source

import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.semantic.type.Visibility
import org.elixir_lang.semantic.type.definition.Source

class ModuleAttribute(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>, identifierName: String) :
    Source(atUnqualifiedNoParenthesesCall) {
    val visibility: Visibility = when (identifierName) {
        "opaque" -> Visibility.OPAQUE
        "typep" -> Visibility.PRIVATE
        else -> Visibility.PUBLIC
    }
}
