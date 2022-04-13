package org.elixir_lang.beam.psi.impl

import com.intellij.psi.stubs.StubBase
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes
import org.elixir_lang.beam.psi.stubs.TypeDefinitionStub

/**
 * A call definition stub fakes a `def` or `defmacro`.
 */
class TypeDefinitionStubImpl<T : TypeDefinition>(parentStub: ModuleStubImpl<ModuleImpl<*>>) :
    StubBase<T>(parentStub, ModuleStubElementTypes.TYPE_DEFINITION), TypeDefinitionStub<T> {
    override fun getName(): String {
        TODO("Not yet implemented")
    }
}
