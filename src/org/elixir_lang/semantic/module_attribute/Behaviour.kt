package org.elixir_lang.semantic.module_attribute

import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.semantic.module_attribute.definition.ModuleAttribute

class Behaviour(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>) :
        ModuleAttribute(atUnqualifiedNoParenthesesCall)
