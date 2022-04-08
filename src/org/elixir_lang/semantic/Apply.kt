package org.elixir_lang.semantic

import org.elixir_lang.semantic.call.definition.clause.Using

interface Apply : Semantic {
    val using: Using
}
