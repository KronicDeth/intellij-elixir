package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Char {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            toMacroString(term).let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "char"

    private fun codePointToMacroString(term: OtpErlangLong): String {
        val macroStringBuilder = StringBuilder().append('?')
                val codePoint = term.intValue()

        when (codePoint) {
            '\n'.toInt() -> macroStringBuilder.append("\\n")
            '\r'.toInt() -> macroStringBuilder.append("\\r")
            else -> macroStringBuilder.appendCodePoint(codePoint)
        }

        return macroStringBuilder.toString()
    }

    private fun codePointToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> codePointToMacroString(term)
                else -> "unknown_code_point"
            }

    private fun toCodePoint(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple): String =
            toCodePoint(term)
                    ?.let { codePointToMacroString(it) }
                    ?: "missing_code_point"
}
