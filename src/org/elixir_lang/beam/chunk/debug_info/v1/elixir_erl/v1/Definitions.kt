package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.MacroNameArity
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.V1
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Definition

class Definitions(val definitionList: List<Definition>) {
    operator fun get(index: Int): Definition = definitionList[index]
    operator fun get(macroNameArity: MacroNameArity): Definition? = definitionByMacroNameArity[macroNameArity]

    fun indexOf(definition: Definition): Int = definitionList.indexOf(definition)
    fun size() = definitionList.size

    val definitionByMacroNameArity by lazy {
        definitionList
                .mapNotNull { definition ->
                    definition.macro?.let { macro ->
                        definition.name?.let { name ->
                            definition.arity?.let { arity ->
                                MacroNameArity(macro, name, arity) to definition
                            }
                        }
                    }
                }
                .toMap()
    }

    companion object {
        fun from(term: OtpErlangObject?, debugInfo: V1): Definitions? =
            if (term is OtpErlangList) {
                from(term, debugInfo)
            } else {
                null
            }

        private fun from(list: OtpErlangList, debugInfo: V1): Definitions =
            Definitions(list.mapNotNull { Definition.from(it, debugInfo) })
    }
}
