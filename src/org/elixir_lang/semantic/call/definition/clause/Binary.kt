package org.elixir_lang.semantic.call.definition.clause

import com.intellij.psi.PsiElement
import org.elixir_lang.NameArityInterval
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause

class Binary(val callDefinition: CallDefinition) : Clause {
    override val psiElement: PsiElement
        get() = callDefinition
    override val enclosingModular: Modular
        get() = TODO("Not yet implemented")
    override val definition: Definition
        get() = TODO("Not yet implemented")
    override val compiled: Boolean = true
    override val nameArityInterval: NameArityInterval = callDefinition.nameArity.toNameArityInterval()
}
