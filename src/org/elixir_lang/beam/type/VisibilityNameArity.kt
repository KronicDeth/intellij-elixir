package org.elixir_lang.beam.type

import org.elixir_lang.Arity
import org.elixir_lang.type.Visibility

data class VisibilityNameArity(val visibility: Visibility, val name: String, val arity: Arity) :
    Comparable<VisibilityNameArity> {
    override fun compareTo(other: VisibilityNameArity): Int =
        compareValuesBy(
            this,
            other,
            VisibilityNameArity::visibility,
            VisibilityNameArity::name,
            VisibilityNameArity::arity
        )
}
