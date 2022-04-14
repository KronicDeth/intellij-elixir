package org.elixir_lang.beam.psi

import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.Arity
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.CanonicallyNamed
import org.elixir_lang.type.Visibility

interface TypeDefinition : CanonicallyNamed, NamedElement, PsiCompiledElement {
    val visibility: Visibility
    val arity: Arity
}
