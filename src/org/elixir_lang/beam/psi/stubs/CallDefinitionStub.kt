package org.elixir_lang.beam.psi.stubs

import com.intellij.psi.stubs.StubElement
import org.elixir_lang.NameArity
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.beam.psi.CallDefinition
import org.elixir_lang.semantic.call.definition.clause.Time

/**
 * A call definition stub fakes a `def` or `defmacro`.
 */
interface CallDefinitionStub<T : CallDefinition> : StubElement<T> {
    val visibility: Visibility
    val time: Time
    val nameArity: NameArity
}
