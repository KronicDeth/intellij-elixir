package org.elixir_lang.semantic.exception.call.definition

import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.exception.call.Definition

class Clause(override val definition: Definition) : Clause {
    override val psiElement: Call
        get() = definition.exception.call
    override val enclosingModular: Modular
        get() = definition.enclosingModular
    override val compiled: Boolean
        get() = TODO("Not yet implemented")
    override val nameArityInterval: NameArityInterval?
        get() = TODO("Not yet implemented")
}
