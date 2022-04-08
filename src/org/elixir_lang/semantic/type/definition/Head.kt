package org.elixir_lang.semantic.type.definition

import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.type.definition.head.Parameter

interface Head : Semantic {
    val parameters: List<Parameter>
}
