package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Match {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        // right executes before left
        val rightMacroString = rightMacroString(term, scope)
        val (leftMacroString, leftDeclaredScope) = leftMacroStringDeclaredScope(term, scope)

        return MacroStringDeclaredScope("$leftMacroString = $rightMacroString", leftDeclaredScope)
    }

    private const val TAG = "match"

    private fun leftMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toLeft(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope.copy(pinning = true)) }
                    ?: MacroStringDeclaredScope("missing_left", Scope.EMPTY)

    private fun rightMacroString(term: OtpErlangTuple, scope: Scope): String =
            toRight(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "missing_right"

    private fun toLeft(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toRight(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
