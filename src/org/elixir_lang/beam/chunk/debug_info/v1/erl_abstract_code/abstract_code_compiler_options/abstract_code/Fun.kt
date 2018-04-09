package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.`fun`.Clauses
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.`fun`.Function

object Fun {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        toMacroString(term, scope)
                .let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private const val TAG = "fun"

    private fun argumentMacroString(term: OtpErlangTuple, scope: Scope): MacroString =
            toArgument(term).let { argumentToMacroString(it, scope) }

    private fun argumentToMacroString(argument: OtpErlangObject, scope: Scope): MacroString =
            Function.ifToMacroString(argument, scope) ?:
            Clauses.ifToMacroString(argument, scope) ?:
            "unknown_fun_argument"

    private fun toArgument(term: OtpErlangTuple): OtpErlangObject = term.elementAt(2)

    private fun toMacroString(term: OtpErlangTuple, scope: Scope): MacroString =
            term.arity().let { arity ->
                when (arity) {
                    3 -> argumentMacroString(term, scope)
                    else -> "unknown_function_arity($arity)"
                }
            }
}
