package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Float {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            toString(term).let { MacroStringDeclaredScope(it, doBlock = false, Scope.EMPTY) }

    private const val TAG = "float"

    private fun floatToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangDouble -> term.doubleValue().toString()
                is OtpErlangFloat -> term.floatValue().toString()
                else -> "unknown_float"
            }

    private fun toString(term: OtpErlangTuple): String =
            toFloat(term)
                    ?.let { floatToString(it) }
                    ?: "unknown_float"

    private fun toFloat(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
