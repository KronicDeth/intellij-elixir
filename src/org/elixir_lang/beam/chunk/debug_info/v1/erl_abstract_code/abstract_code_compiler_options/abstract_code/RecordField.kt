package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.code.Identifier.inspectAsKey

object RecordField {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangTuple -> toMacroString(term)
                else -> "unknown_record_field"
            }

    fun toMacroString(term: OtpErlangTuple): String {
        val fieldMacroString = fieldMacroString(term)
        val expressionMacroString = expressionMacroString(term)

        return "$fieldMacroString $expressionMacroString"
    }

    private const val TAG = "record_field"

    private fun expressionMacroString(term: OtpErlangTuple): String =
            toExpression(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "missing_expression"

    private fun fieldMacroString(term: OtpErlangTuple): String =
            toField(term)
                    ?.let { fieldToMacroString(it) }
                    ?: "missing_field:"

    private fun fieldToMacroString(term: OtpErlangObject): String =
            Atom.toElixirAtom(term)
                    ?.let { inspectAsKey(it) }
                    ?: "unknown_field:"

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toField(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
