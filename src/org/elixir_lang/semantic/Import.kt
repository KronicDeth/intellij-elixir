package org.elixir_lang.semantic

import org.elixir_lang.semantic.call.Definition

interface Import : Semantic {
    val importedCallDefinitions: kotlin.collections.List<Definition>
}
