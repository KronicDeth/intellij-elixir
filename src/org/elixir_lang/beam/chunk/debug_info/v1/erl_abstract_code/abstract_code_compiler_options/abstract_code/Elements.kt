package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

fun List<MacroStringDeclaredScope>.unzip(): Pair<List<MacroString>, List<DeclaredScope>> {
    val macroStringList = mutableListOf<MacroString>()
    val declaredScopeList = mutableListOf<DeclaredScope>()

    for ((macroString, declaredScope) in this) {
        macroStringList.add(macroString)
        declaredScopeList.add(declaredScope)
    }

    return macroStringList to declaredScopeList
}

object Elements {
    fun toMacroStringDeclaredScope(elements: OtpErlangList, scope: Scope): MacroStringDeclaredScope =
            elements
                    .map { AbstractCode.toMacroStringDeclaredScope(it, scope) }
                    .unzip()
                    .let { (macroStrings, declaredScopes) ->
                        val macroString = macroStrings.joinToString(", ")
                        val declaredScope = declaredScopes.fold(Scope.EMPTY, Scope::union)

                        MacroStringDeclaredScope(macroString, declaredScope)
                    }

    fun toMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope) =
            when (term) {
                is OtpErlangList -> toMacroStringDeclaredScope(term, scope)
                else -> MacroStringDeclaredScope("unknown_elements", Scope.EMPTY)
            }
}
