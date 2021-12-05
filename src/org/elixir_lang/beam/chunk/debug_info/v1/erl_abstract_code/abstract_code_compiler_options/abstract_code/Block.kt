package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

/**
 * ```erlang
 * begin
 *   ...
 * end
 * ```
 *
 * is just a way to parenthesize multiple expressions to when only 1 is allowed in Erlang
 */
object Block {
    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T) = ifTag(term, TAG, ifTrue)


    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: DeclaredScope): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it, scope) }

    private const val TAG = "block"

    private fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: DeclaredScope): MacroStringDeclaredScope =
            MacroStringDeclaredScope(toString(term, scope), doBlock = false, Scope.EMPTY)

    fun toString(term: OtpErlangTuple, scope: DeclaredScope): String =
            toExpressions(term)
                    ?.let { expressionsToMacroStringDeclaredScope(it, scope) }
                    ?: AbstractCode.unknown("block", "block", term)

    private fun toExpressions(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)

    private fun expressionsToMacroStringDeclaredScope(expressions: OtpErlangObject, scope: DeclaredScope): String =
           Sequence
                   .toMacroStringDeclaredScope(expressions, scope.copy(pinning = false), "(", Separator("; ", group = false), ")")
                   .macroString
                   .string
}
