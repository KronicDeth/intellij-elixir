package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.code.Identifier.inspectAsFunction


object RecordIndex {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_record_index"
            }

    fun toMacroString(term: OtpErlangTuple): String {
        val nameMacroString = nameMacroString(term)
        val fieldMacroString = fieldMacroString(term)

        return "$nameMacroString($fieldMacroString)"
    }

    private const val TAG = "record_index"

    private fun fieldMacroString(term: OtpErlangTuple): String =
            toField(term)
                    ?.let { fieldToMacroString(it) }
                    ?: "missing_field"

    private fun fieldToMacroString(term: OtpErlangObject): String = AbstractCode.toMacroString(term)

    private fun nameMacroString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

    private fun nameToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> inspectAsFunction(term)
                else -> "unknown_name"
            }

    private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toField(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
