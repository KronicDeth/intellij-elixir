package org.elixir_lang.beam.psi.impl

import com.intellij.psi.stubs.StubBase
import com.intellij.util.io.StringRef
import org.elixir_lang.NameArity
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.ModuleDefinitionStub
import org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes
import org.elixir_lang.beam.psi.stubs.TypeDefinitionStub
import org.elixir_lang.beam.psi.stubs.TypeDefinitionType
import org.elixir_lang.semantic.type.Visibility

/**
 * A type definition stub fakes a `@type` or `@typep`.
 */
class TypeDefinitionStubImpl<M: ModuleDefinition, T : TypeDefinition, C: CallDefinition>(
        parentStub: ModuleDefinitionStub<M, T, C>,
        override val visibility: Visibility,
        override val nameArity: NameArity
) : StubBase<T>(parentStub, ModuleStubElementTypes.TYPE_DEFINITION), TypeDefinitionStub<T> {
    constructor(parentStub: ModuleDefinitionStub<M, T, C>,
                visibility: StringRef,
                name: StringRef,
                arity: Int) :
            this(parentStub, Visibility.valueOf(visibility.toString()), NameArity(name.toString(), arity))
}
