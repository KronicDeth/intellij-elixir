package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Integer {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            toString(term)
                    .let { MacroStringDeclaredScope(it, doBlock = false, Scope.EMPTY) }

    private const val TAG = "integer"

    private fun integerToString(term: OtpErlangLong): String = term.bigIntegerValue().toString()

    private fun integerToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> integerToString(term)
                else -> AbstractCode.unknown("integer", "integer", term)
            }

    private fun toInteger(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toString(term: OtpErlangTuple): String =
            toInteger(term)
                ?.let { integerToString(it) }
                ?: AbstractCode.missing("integer", "integer", term)
}
