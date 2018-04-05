package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Integer {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            toMacroString(term)
                    .let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "integer"

    private fun integerToMacroString(term: OtpErlangLong): String = term.bigIntegerValue().toString()

    private fun integerToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> integerToMacroString(term)
                else -> "unknown_integer"
            }

    private fun toInteger(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple): String =
            toInteger(term)
                ?.let { integerToMacroString(it) }
                ?: "missing_integer"
}
