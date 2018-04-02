package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag

object Tuple {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            elementsMacroStringDeclaredScope(term, scope).let { (elementsMacroString, elementsDeclaredScope) ->
                MacroStringDeclaredScope("{$elementsMacroString}", elementsDeclaredScope)
            }


    private const val TAG = "tuple"

    private fun elementsMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope) =
            toElements(term)
                    ?.let { Elements.toMacroStringDeclaredScope(it, scope) }
                    ?: MacroStringDeclaredScope("missing_elements", Scope.EMPTY)

    private fun toElements(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
