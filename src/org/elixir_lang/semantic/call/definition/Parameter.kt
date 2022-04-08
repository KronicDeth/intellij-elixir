package org.elixir_lang.semantic.call.definition

import org.elixir_lang.semantic.Semantic

interface Parameter : Semantic {
    val head: Semantic
}
