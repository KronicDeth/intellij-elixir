package org.elixir_lang.beam.psi

import com.intellij.psi.PsiCompiledElement
import org.elixir_lang.psi.NamedElement

interface ModuleDefinition : NamedElement, PsiCompiledElement {
    override fun getName(): String
    val typeDefinitions: List<TypeDefinition>
    val callDefinitions: List<CallDefinition>
}
