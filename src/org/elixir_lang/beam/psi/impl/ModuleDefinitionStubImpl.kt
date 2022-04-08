package org.elixir_lang.beam.psi.impl

import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.*

class ModuleDefinitionStubImpl< M : ModuleDefinition, T: TypeDefinition, C: CallDefinition>(
        parentStub: StubElement<*>,
        override val name: String
) : StubBase<M>(parentStub, ModuleStubElementTypes.MODULE_DEFINITION), ModuleDefinitionStub<M, T, C> {
    override val typeDefinitionStubs: List<TypeDefinitionStub<T>>
      get() = childrenStubs.filterIsInstance<TypeDefinitionStub<T>>()
    override val callDefinitionStubs: List<CallDefinitionStub<C>>
      get() = childrenStubs.filterIsInstance<CallDefinitionStub<C>>()
}
