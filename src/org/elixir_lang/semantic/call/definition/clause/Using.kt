package org.elixir_lang.semantic.call.definition.clause

import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause

interface Using : Clause {
    val exportedCallDefinitions: List<Definition>
}
