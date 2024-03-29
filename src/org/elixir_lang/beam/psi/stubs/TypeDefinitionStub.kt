package org.elixir_lang.beam.psi.stubs

import com.intellij.psi.stubs.StubElement
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.type.Visibility

interface TypeDefinitionStub<T : TypeDefinition?> : StubElement<T> {
    val visibility: Visibility
    val name: String
    val arity: Int
}
