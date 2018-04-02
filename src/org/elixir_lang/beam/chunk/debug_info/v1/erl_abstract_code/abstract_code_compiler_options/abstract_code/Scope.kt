package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

data class Scope(val varNameSet: Set<String>, val pinning: Boolean = false) {
    fun union(other: Scope): Scope {
        val otherVarNameSet = other.varNameSet
        val otherPinning = other.pinning
        val pinningUnion = pinning || otherPinning

        return when {
            pinning == pinningUnion && varNameSet.containsAll(otherVarNameSet) -> this
            otherPinning == pinningUnion && otherVarNameSet.containsAll(varNameSet) -> other
            else ->
                Scope(varNameSet.union(otherVarNameSet), pinningUnion)
        }
    }

    companion object {
        val EMPTY = Scope(emptySet())
    }
}
