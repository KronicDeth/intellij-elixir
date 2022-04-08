package org.elixir_lang.beam.psi

import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.NameArity
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.psi.NamedElement

interface CallDefinition : NamedElement, PsiCompiledElement {
    val visibility: Visibility
    val time: Time
    val nameArity: NameArity
}
