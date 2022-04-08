package org.elixir_lang.beam.psi

import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.NameArity
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.semantic.type.Visibility

interface TypeDefinition : NamedElement, PsiCompiledElement {
    val visibility: Visibility
    override fun getName(): String? = nameArity.name
    val nameArity: NameArity
}
