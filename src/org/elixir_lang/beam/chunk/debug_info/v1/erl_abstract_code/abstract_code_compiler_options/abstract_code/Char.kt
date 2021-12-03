package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Char {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
            toString(term).let { MacroStringDeclaredScope(it, doBlock = false, Scope.EMPTY) }

    private const val TAG = "char"

    private fun codePointToString(term: OtpErlangLong): String {
        val macroStringBuilder = StringBuilder().append('?')
        val codePoint = term.intValue()

        when (codePoint) {
            '\\'.toInt() -> macroStringBuilder.append("\\\\")
            '\n'.toInt() -> macroStringBuilder.append("\\n")
            '\r'.toInt() -> macroStringBuilder.append("\\r")
            ' '.toInt() -> macroStringBuilder.append("\\s")
            '\t'.toInt() -> macroStringBuilder.append("\\t")
            else -> macroStringBuilder.appendCodePoint(codePoint)
        }

        return macroStringBuilder.toString()
    }

    private fun codePointToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> codePointToString(term)
                else -> "unknown_code_point"
            }

    private fun toCodePoint(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toString(term: OtpErlangTuple): String =
            toCodePoint(term)
                    ?.let { codePointToString(it) }
                    ?: "missing_code_point"
}
