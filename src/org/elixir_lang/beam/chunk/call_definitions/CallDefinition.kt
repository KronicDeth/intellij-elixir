package org.elixir_lang.beam.chunk.call_definitions

import org.elixir_lang.beam.chunk.Atoms

// atomIndex is 1-based
data class CallDefinition(private val atomIndex: Long, val arity: Long) {
    fun arity(): Long = arity
    fun name(atoms: Atoms): String? = atoms.get(atomIndex.toInt())?.string
}
