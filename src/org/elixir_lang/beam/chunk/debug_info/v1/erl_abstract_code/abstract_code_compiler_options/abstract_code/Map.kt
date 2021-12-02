package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag


object Map {
    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTag(term, TAG) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            when (term.arity()) {
                3 -> constructionToMacroStringDeclaredScope(term, scope)
                4 -> updateToMacroStringDeclaredScope(term, scope)
                else -> MacroStringDeclaredScope( "unknown_map_operation", scope)
            }

    private const val TAG = "map"

    private fun constructionToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            associationsMacroString(term, 2, scope).let { (associationsMacroString, associationsDeclaredScope) ->
                MacroStringDeclaredScope("%{${associationsMacroString}}", associationsDeclaredScope)
            }

    private fun updateToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            associationsMacroString(term, 3, scope).let { (associationsMacroString, associationsDeclaredScope) ->
                val sourceMacroString = sourceMacroString(term, scope)

                MacroStringDeclaredScope("%{${sourceMacroString} | ${associationsMacroString}}", associationsDeclaredScope)
            }

    private fun sourceMacroString(term: OtpErlangTuple, scope: Scope) =
            term.elementAt(2)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: "unknown_map_update_source"

    private fun associationsMacroString(term: OtpErlangTuple, index: Int, scope: Scope): MacroStringDeclaredScope =
            term.elementAt(index)
                ?.let { Elements.toMacroStringDeclaredScope(it, scope) }
                ?: MacroStringDeclaredScope("missing_associations", Scope.EMPTY)
}
