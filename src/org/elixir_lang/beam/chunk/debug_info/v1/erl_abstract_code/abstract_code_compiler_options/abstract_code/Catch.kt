package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Catch {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        toMacroString(term, scope).let { macroString ->
            MacroStringDeclaredScope(macroString, Scope.EMPTY)
        }

    private const val TAG = "catch"

    private fun bodyString(term: OtpErlangTuple, scope: Scope): String =
            toBody(term)
                    ?.let { AbstractCode.toString(it, scope) }
                    ?: AbstractCode.missing("catch_body", "catch body", term)

    private fun toBody(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val bodyString = bodyString(term, scope)
        val string = "try do\n" +
                "  $bodyString\n" +
                "catch\n" +
                "  error -> error\n" +
                "end"

        return MacroString(string, doBlock = true)
    }
}
