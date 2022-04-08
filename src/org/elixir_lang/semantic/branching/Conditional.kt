package org.elixir_lang.semantic.branching

import org.elixir_lang.semantic.Branching
import org.elixir_lang.semantic.Semantic

interface Conditional : Branching {
    val condition: Semantic
}
