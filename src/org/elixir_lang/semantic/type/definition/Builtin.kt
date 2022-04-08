package org.elixir_lang.semantic.type.definition

import org.elixir_lang.NameArity

object Builtin {
    fun `is`(name: String, arity: Int) = ARITIES_BY_NAME[name]?.contains(arity) ?: false

    @JvmStatic
    val ARITIES_BY_NAME: Map<String, Set<Int>> = mapOf(
            "any" to setOf(0),
            "arity" to setOf(0),
            "as_boolean" to setOf(1),
            "atom" to setOf(0),
            "binary" to setOf(0),
            "bitstring" to setOf(0),
            "boolean" to setOf(0),
            "byte" to setOf(0),
            "char" to setOf(0),
            "charlist" to setOf(0),
            "float" to setOf(0),
            "fun" to setOf(0),
            "function" to setOf(0),
            "identifier" to setOf(0),
            "integer" to setOf(0),
            "iodata" to setOf(0),
            "iolist" to setOf(0),
            "keyword" to setOf(0, 1),
            "list" to setOf(0, 1),
            "map" to setOf(0),
            "maybe_improper_list" to setOf(0, 2),
            "mfa" to setOf(0),
            "module" to setOf(0),
            "neg_integer" to setOf(0),
            "no_return" to setOf(0),
            "node" to setOf(0),
            "non_empty_list" to setOf(0, 1),
            "non_neg_integer" to setOf(0),
            "none" to setOf(0),
            "nonempty_charlist" to setOf(0),
            "nonempty_improper_list" to setOf(0, 2),
            "nonempty_maybe_improper_list" to setOf(2),
            "number" to setOf(0),
            "pid" to setOf(0),
            "port" to setOf(0),
            "pos_integer" to setOf(0),
            "reference" to setOf(0),
            "struct" to setOf(0),
            "term" to setOf(0),
            "timeout" to setOf(0),
            "tuple" to setOf(0)
    )
    val SORTED_NAME_ARITIES: List<NameArity> =
            ARITIES_BY_NAME
                    .flatMap { (name, arities) ->
                        arities.map { arity ->
                            NameArity(name, arity)
                        }
                    }
                    .sorted()
}
