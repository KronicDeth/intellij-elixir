package org.elixir_lang.beam.psi

import org.elixir_lang.Arity
import org.elixir_lang.type.Visibility

interface TypeDefinition : BeamSymbol {
    /** Never null: the stub always carries one, which is what makes this indexable by name. */
    override fun getName(): String

    /** The decompiled module this definition belongs to. */
    override fun getParent(): Module

    val visibility: Visibility
    val arity: Arity
}
