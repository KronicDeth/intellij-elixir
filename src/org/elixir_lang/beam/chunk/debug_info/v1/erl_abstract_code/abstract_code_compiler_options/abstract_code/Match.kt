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

        // Propagate doBlock from the right-hand side so that callers know to wrap in
        // parentheses when needed — e.g. `cs = for ... end` used as a comprehension
        // qualifier must become `(cs = for ... end)` to prevent `end do` syntax errors.
        return MacroStringDeclaredScope("${leftMacroString.string} = ${rightMacroString.string}", doBlock = rightMacroString.doBlock, leftDeclaredScope)
    }

    private const val TAG = "match"

    private fun leftMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toLeft(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope.copy(pinning = true)) }
                    ?: MacroStringDeclaredScope.missing("left", "match left", term)

    private fun rightMacroString(term: OtpErlangTuple, scope: Scope): MacroString =
            toRight(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: MacroString.error("missing_right", "match right", term)

    private fun toLeft(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toRight(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
}
