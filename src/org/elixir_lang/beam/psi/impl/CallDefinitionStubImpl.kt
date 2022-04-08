package org.elixir_lang.beam.psi.impl

import com.intellij.psi.stubs.StubBase
import org.elixir_lang.NameArity
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.beam.psi.TypeDefinition
import org.elixir_lang.beam.psi.stubs.*
import org.elixir_lang.semantic.call.definition.clause.Time

/**
 * A call definition stub fakes a `def` or `defmacro`.
 */
class CallDefinitionStubImpl<M: ModuleDefinition,
                             T: TypeDefinition,
                             C: CallDefinition>(parentStub: ModuleDefinitionStub<M, T, C>,
                                                override val visibility: Visibility,
                                                override val time: Time,
                                                override val nameArity: NameArity) : StubBase<C>(parentStub, ModuleStubElementTypes.CALL_DEFINITION), CallDefinitionStub<C>
