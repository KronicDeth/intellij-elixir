package org.elixir_lang.semantic.call

import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.definition.clause.Using

interface Usage : Semantic {
    val using: Using
}
