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
                else -> MacroStringDeclaredScope.unknown( "map_operation", "map", term)
            }

    private const val TAG = "map"

    private fun constructionToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            associationsMacroString(term, 2, scope).let { (associationsMacroString, associationsDeclaredScope) ->
                MacroStringDeclaredScope("%{${associationsMacroString.string}}", doBlock = false, associationsDeclaredScope)
            }

    private fun updateToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope =
            associationsMacroString(term, 3, scope).let { (associationsMacroString, associationsDeclaredScope) ->
                val sourceString = sourceMacroString(term, scope).group().string

                MacroStringDeclaredScope("%{${sourceString} | ${associationsMacroString.string}}", doBlock = false, associationsDeclaredScope)
            }

    private fun sourceMacroString(term: OtpErlangTuple, scope: Scope): MacroString =
            term.elementAt(2)
                    ?.let { AbstractCode.toMacroStringDeclaredScope(it, scope).macroString }
                    ?: MacroString.unknown("map_update_source", "map update source", term)

    private fun associationsMacroString(term: OtpErlangTuple, index: Int, scope: Scope): MacroStringDeclaredScope =
            term.elementAt(index)
                ?.let { Sequence.toCommaSeparatedMacroStringDeclaredScope(it, scope) }
                ?: MacroStringDeclaredScope.missing("associations", "map associations", term)
}
