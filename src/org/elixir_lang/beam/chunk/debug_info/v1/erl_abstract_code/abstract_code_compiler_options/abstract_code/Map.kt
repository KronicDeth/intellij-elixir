package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode


object Map {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val associationsMacroString = associationsMacroString(term)

        return "%{$associationsMacroString}"
    }

    private const val TAG = "map"

    private fun associationsMacroString(term: OtpErlangTuple): String =
        toAssociations(term)
                ?.let { associationsToMacroString(it) }
                ?: "missing_associations"

    private fun associationsToMacroString(term: OtpErlangList): String =
            term.joinToString(", ") { AbstractCode.toMacroString(it) }

    private fun associationsToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> associationsToMacroString(term)
                else -> "unknown_associations"
            }

    private fun toAssociations(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
