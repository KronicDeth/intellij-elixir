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

    private fun bodyMacroString(term: OtpErlangTuple, scope: Scope) =
            toBody(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "missing_catch_body"

    private fun toBody(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString {
        val bodyMacroString = bodyMacroString(term, scope)

        return "try do\n" +
                "  $bodyMacroString\n" +
                "catch\n" +
                "  error -> error\n" +
                "end"
    }
}
