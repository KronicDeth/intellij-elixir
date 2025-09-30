package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Char {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject?): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple): MacroStringDeclaredScope =
        MacroStringDeclaredScope(toString(term), doBlock = false, Scope.EMPTY)

    private const val TAG = "char"

    private fun codePointToString(term: OtpErlangLong): String {
        val macroStringBuilder = StringBuilder().append('?')
        when (val codePoint = term.intValue()) {
            '\\'.code -> macroStringBuilder.append("\\\\")
            '\n'.code -> macroStringBuilder.append("\\n")
            '\r'.code -> macroStringBuilder.append("\\r")
            ' '.code -> macroStringBuilder.append("\\s")
            '\t'.code -> macroStringBuilder.append("\\t")
            else -> macroStringBuilder.appendCodePoint(codePoint)
        }

        return macroStringBuilder.toString()
    }

    private fun codePointToString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangLong -> codePointToString(term)
                else -> AbstractCode.unknown("code_point", "char code point", term)
            }

    private fun toCodePoint(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toString(term: OtpErlangTuple): String =
            toCodePoint(term)
                    ?.let { codePointToString(it) }
                    ?: AbstractCode.missing("code_point", "char code point", term)
}
