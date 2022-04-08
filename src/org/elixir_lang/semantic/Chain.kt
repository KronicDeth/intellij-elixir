package org.elixir_lang.semantic

import org.elixir_lang.semantic.chain.Clause

/**
 * A special form that takes `<-` and `=` clauses
 */
interface Chain : Semantic {
    val clauses: kotlin.collections.List<Clause>
}
