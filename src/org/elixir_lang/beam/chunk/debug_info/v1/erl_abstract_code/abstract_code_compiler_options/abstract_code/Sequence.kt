package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifier
import org.elixir_lang.beam.decompiler.MacroNameArity

object Sequence {
    fun toMacroStringDeclaredScope(term: OtpErlangList,
                                   scope: Scope,
                                   open: String, separator: String, close: String): MacroStringDeclaredScope =
            toMacroStringDeclaredScope(term, scope) { macroStringList ->
                val separatedMacroString = macroStringList.joinToString(separator)

                if (macroStringList.size > 1) {
                    "$open$separatedMacroString$close"
                } else {
                    separatedMacroString
                }
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject, decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity): MacroStringDeclaredScope =
            toMacroStringDeclaredScope(term, Scope.EMPTY.copy(pinning = true)) { macroStringList ->
                val macroStringBuilder = StringBuilder()

                decompiler.appendSignature(macroStringBuilder, macroNameArity, macroNameArity.name, macroStringList.toTypedArray())

                macroStringBuilder.toString()
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject,
                                   scope: Scope,
                                   joiner: (List<MacroString>) -> MacroString): MacroStringDeclaredScope =
            when (term) {
                is OtpErlangList -> toMacroStringDeclaredScope(term, scope, joiner)
                else -> unknown(scope)
            }

    fun unknown() = unknown(Scope.EMPTY)
    fun unknown(scope: Scope) = MacroStringDeclaredScope("unknown_sequence", scope)

    fun toMacroStringDeclaredScope(term: OtpErlangList,
                                   scope: Scope,
                                   joiner: (List<MacroString>) -> MacroString): MacroStringDeclaredScope =
            term.fold(Pair(mutableListOf<MacroString>(), scope)) { (accMacroStringList, accScope), qualifier ->
                val (qualifierMacroString, qualifierDeclaredScope) = Qualifier.toMacroStringDeclaredScope(qualifier, accScope)
                accMacroStringList.add(qualifierMacroString)

                Pair(accMacroStringList, accScope.union(qualifierDeclaredScope))
            }.let { (macroStringList, declaredScope) ->
                val macroString = joiner(macroStringList)

                MacroStringDeclaredScope(macroString, declaredScope)
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject?,
                                   scope: Scope,
                                   open: String, separator: String, close: String): MacroStringDeclaredScope =
            when (term) {
                is OtpErlangList -> toMacroStringDeclaredScope(term, scope, open, separator, close)
                else -> MacroStringDeclaredScope("unknown_sequence", Scope.EMPTY)
            }
}
