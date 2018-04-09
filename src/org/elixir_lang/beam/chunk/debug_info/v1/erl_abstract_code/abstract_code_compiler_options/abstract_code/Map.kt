package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag


object Map {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        associationsMacroString(term, scope).let { (associationsMacroString, associationsDeclaredScope) ->
            MacroStringDeclaredScope("%{$associationsMacroString}", associationsDeclaredScope)
        }

    private const val TAG = "map"

    private fun associationsMacroString(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
        toAssociations(term)
                ?.let { Elements.toMacroStringDeclaredScope(it, scope) }
                ?: MacroStringDeclaredScope("missing_associations", Scope.EMPTY)

    private fun toAssociations(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
