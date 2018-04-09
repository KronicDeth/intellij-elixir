package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifier

object Sequence {
    fun toMacroStringDeclaredScope(term: OtpErlangList, scope: Scope, separator: String): MacroStringDeclaredScope =
            term.fold(Pair(mutableListOf<MacroString>(), scope)) { (accMacroStringList, accScope), qualifier ->
                val (qualifierMacroString, qualifierDeclaredScope) = Qualifier.toMacroStringDeclaredScope(qualifier, accScope)
                accMacroStringList.add(qualifierMacroString)

                Pair(accMacroStringList, accScope.union(qualifierDeclaredScope))
            }.let { (macroStringList, declaredScope) ->
                val macroString = macroStringList.joinToString(separator)

                MacroStringDeclaredScope(macroString, declaredScope)
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject?, scope: Scope, separator: String): MacroStringDeclaredScope =
            when (term) {
                is OtpErlangList -> toMacroStringDeclaredScope(term, scope, separator)
                else -> MacroStringDeclaredScope("unknown_sequence", Scope.EMPTY)
            }
}
