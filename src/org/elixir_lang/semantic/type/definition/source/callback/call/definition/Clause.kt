package org.elixir_lang.semantic.type.definition.source.callback.call.definition

import com.intellij.psi.PsiElement
import org.elixir_lang.NameArityInterval
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.type.definition.source.callback.call.Definition

class Clause(override val definition: Definition) :
    org.elixir_lang.semantic.call.definition.Clause {
    override val enclosingModular: Modular
        get() = definition.enclosingModular
    override val psiElement: PsiElement
        get() = definition.callback.psiElement
    override val compiled: Boolean = false
    override val nameArityInterval: NameArityInterval
        get() = definition.nameArityInterval
}
