package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.qualifier

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Bin
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroStringDeclaredScope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

/**
 * The `b_generate` bitstring comprehension generator, `<<Pattern <- Expression>>`.
 */
object BitstringGenerate : Generator("b_generate", "bitstring generate expression") {
    override fun patternMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            toPattern(term)
                    ?.let { patternToMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope.missing("pattern", scope, "bitstring generate pattern", term)

    override fun macroString(patternMacroString: MacroString, expressionMacroString: MacroString): String {
        val expressionString = expressionMacroString.group().string
        // When the expression is itself a bitstring (`<<...>>`), the closing `>>>>` is
        // ambiguous because `>>>` is parsed as Elixir's unsigned-right-shift operator.
        // Wrap in parentheses to disambiguate: `<<c :: 6 <- (<<bin :: binary>>)>>`
        val safeExpressionString = if (expressionString.startsWith("<<")) {
            "($expressionString)"
        } else {
            expressionString
        }

        return "<<${patternMacroString.string} <- $safeExpressionString>>"
    }

    private fun patternToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope) =
            Bin.ifTo(term) {
                Bin.ifBinElementsToMacroStringDeclaredScope(it, scope)
            } ?:
            AbstractCode.toMacroStringDeclaredScope(term, scope)
}
