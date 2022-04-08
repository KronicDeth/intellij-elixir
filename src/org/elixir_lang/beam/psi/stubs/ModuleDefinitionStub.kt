package org.elixir_lang.beam.psi.stubs

import com.intellij.psi.stubs.StubElement
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition

interface ModuleDefinitionStub<M : ModuleDefinition, T: TypeDefinition, C: CallDefinition> : StubElement<M> {
    val name: String
    val typeDefinitionStubs: List<TypeDefinitionStub<T>>
    val callDefinitionStubs: List<CallDefinitionStub<C>>
}
