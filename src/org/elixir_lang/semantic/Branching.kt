package org.elixir_lang.semantic

interface Branching : Semantic {
    val branches: kotlin.collections.List<Semantic>
}
