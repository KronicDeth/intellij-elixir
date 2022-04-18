package org.elixir_lang.beam.psi.impl

import com.intellij.psi.stubs.StubBase
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.ModuleStub
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes
import org.elixir_lang.beam.psi.stubs.TypeDefinitionStub
import org.elixir_lang.type.Visibility

/**
 * A call definition stub fakes a `def` or `defmacro`.
 */
class TypeDefinitionStubImpl<T : TypeDefinition>(
    parentStub: ModuleStub<*>,
    override val visibility: Visibility,
    override val name: String,
    override val arity: Int
) : StubBase<T>(parentStub, ModuleStubElementTypes.TYPE_DEFINITION), TypeDefinitionStub<T>
