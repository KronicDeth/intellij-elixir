package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Definition

class Definitions(private val definitionList: List<Definition>) {
    operator fun get(index: Int) = definitionList.get(index)
    fun indexOf(definition: Definition): Int = definitionList.indexOf(definition)
    fun size() = definitionList.size

    companion object {
        fun from(term: OtpErlangObject?): Definitions? =
            if (term is OtpErlangList) {
                from(term)
            } else {
                null
            }

        private fun from(list: OtpErlangList): Definitions =
            Definitions(list.mapNotNull { Definition.from(it) })
    }
}
