package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangString
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.term.inspect

// not named String because I don't want to delay with the name collision with normal `String`
object AbstractCodeString {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String  = stringMacroString(term)

    private const val TAG = "string"

    private fun stringMacroString(term: OtpErlangTuple) =
            toString(term)
                    ?.let { stringToMacroString(it) }
                    ?: "missing_string"

    private fun stringToMacroString(term: OtpErlangObject): String =
        when (term) {
            is OtpErlangString -> stringToMacroString(term)
            else -> "unknown_string"
        }

    private fun stringToMacroString(term: OtpErlangString): String = inspect(term)
    private fun toString(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
