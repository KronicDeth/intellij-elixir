package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

data class Scope(val varNameSet: Set<String>) {
    fun union(other: Scope): Scope {
        val otherVarNameSet = other.varNameSet

        return when {
            varNameSet.containsAll(otherVarNameSet) -> this
            otherVarNameSet.containsAll(varNameSet) -> other
            else -> Scope(varNameSet.union(otherVarNameSet))
        }
    }

    companion object {
        val EMPTY = Scope(emptySet())
    }
}
