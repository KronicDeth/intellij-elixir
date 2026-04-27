package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Bin
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object BitstringGenerate {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val (expressionMacroString, expressionDeclaredScope) = expressionMacroStringDeclaredScope(term, scope)
        val patternScope = scope.union(expressionDeclaredScope)
        val (patternMacroString, patternDeclaredScope) = patternMacroStringDeclaredScope(term, patternScope)

        val expressionString = expressionMacroString.group().string
        // When the expression is itself a bitstring (`<<...>>`), the closing `>>>>` is
        // ambiguous because `>>>` is parsed as Elixir's unsigned-right-shift operator.
        // Wrap in parentheses to disambiguate: `<<c :: 6 <- (<<bin :: binary>>)>>`
        val safeExpressionString = if (expressionString.startsWith("<<")) {
            "($expressionString)"
        } else {
            expressionString
        }

        val string = "<<${patternMacroString.string} <- $safeExpressionString>>"
        val declaredScope = patternScope.union(patternDeclaredScope)

        return MacroStringDeclaredScope(string, doBlock = false, declaredScope)
    }

    private const val TAG = "b_generate"

    private fun expressionMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toExpression(term)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("expression", scope, "bitstring generate expression", term)

    private fun patternMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toPattern(term)
                    ?.let { patternToMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("pattern", scope, "bitstring generate pattern", term)

    private fun patternToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope) =
            Bin.ifTo(term) {
                Bin.ifBinElementsToMacroStringDeclaredScope(it, scope)
            } ?:
            AbstractCode.toMacroStringDeclaredScope(term, scope)

    private fun toExpression(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toPattern(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
