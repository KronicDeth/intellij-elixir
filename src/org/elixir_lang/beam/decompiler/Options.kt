package org.elixir_lang.beam.decompiler

import org.elixir_lang.psi.call.name.Function.*

data class Options(val decompileMacros: Set<String> = setOf(DEFMACRO, DEFMACROP, DEF, DEFP),
                   val decompileBodies: Boolean = false,
                   val decompiledBodyLengthLimit: Int = 1024,
                   val clauseLimit: Int = 10) {
    fun decompileMacro(macro: String): Boolean = macro in decompileMacros
    fun truncateDecompiledBody(decompileBody: String): Boolean = decompileBody.length > decompiledBodyLengthLimit
}
