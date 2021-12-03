package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.comprehension.Qualifier
import org.elixir_lang.beam.decompiler.MacroNameArity

data class Separator(val string: String, val group: Boolean) {
    fun group(macroString: MacroString): String =
            if (group) {
                macroString.group().string
            } else {
                macroString.string
            }

}

val COMMA = Separator(", ", group = true)

object Sequence {
    fun toCommaSeparatedString(term: OtpErlangObject?, scope: Scope = Scope.EMPTY): String =
            when (term) {
                is OtpErlangList -> toCommaSeparatedMacroStringDeclaredScope(term, scope).macroString.string
                else -> "unknown_sequence"
            }

    fun toCommaSeparatedString(term: OtpErlangList, scope: Scope = Scope.EMPTY): String =
            toCommaSeparatedMacroStringDeclaredScope(term, scope).macroString.string

    fun toCommaSeparatedMacroStringDeclaredScope(term: OtpErlangObject?, scope: Scope): MacroStringDeclaredScope =
            when (term) {
                is OtpErlangList -> toCommaSeparatedMacroStringDeclaredScope(term, scope)
                else -> unknown(scope)
            }

    fun toCommaSeparatedMacroStringDeclaredScope(term: OtpErlangList, scope: Scope): MacroStringDeclaredScope =
            toMacroStringDeclaredScope(term, scope, "", COMMA, "")

    fun toMacroStringDeclaredScope(term: OtpErlangList,
                                   scope: Scope,
                                   open: String, separator: Separator, close: String): MacroStringDeclaredScope =
            toMacroStringDeclaredScope(term, scope) { macroStringList ->
                val separatedString =  when (macroStringList.size) {
                    0 -> ""
                    1 -> macroStringList.first().string
                    else -> macroStringList.joinToString(separator.string) { macroString ->
                        separator.group(macroString)
                    }
                }

                "$open$separatedString$close"
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject, decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity): MacroStringDeclaredScope =
            toMacroStringDeclaredScope(term, Scope.EMPTY.copy(pinning = true)) { macroStringList ->
                val macroStringBuilder = StringBuilder()

                decompiler.appendSignature(macroStringBuilder, macroNameArity, macroNameArity.name, macroStringList.map(MacroString::string).toTypedArray())

                macroStringBuilder.toString()
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject,
                                   scope: Scope,
                                   joiner: (List<MacroString>) -> String): MacroStringDeclaredScope =
            when (term) {
                is OtpErlangList -> toMacroStringDeclaredScope(term, scope, joiner)
                else -> unknown(scope)
            }

    fun unknown() = unknown(Scope.EMPTY)
    fun unknown(scope: Scope) = MacroStringDeclaredScope("unknown_sequence", doBlock = false, scope)

    fun toMacroStringDeclaredScope(term: OtpErlangList,
                                   scope: Scope,
                                   joiner: (List<MacroString>) -> String): MacroStringDeclaredScope =
            term.fold(Pair(mutableListOf<MacroString>(), scope)) { (accMacroStringList, accScope), qualifier ->
                val (qualifierMacroString, qualifierDeclaredScope) = Qualifier.toMacroStringDeclaredScope(qualifier, accScope)
                accMacroStringList.add(qualifierMacroString)

                Pair(accMacroStringList, accScope.union(qualifierDeclaredScope))
            }.let { (macroStringList, declaredScope) ->
                val string = joiner(macroStringList)

                MacroStringDeclaredScope(string, doBlock = false, declaredScope)
            }

    fun toMacroStringDeclaredScope(term: OtpErlangObject?,
                                   scope: Scope,
                                   open: String, separator: Separator, close: String): MacroStringDeclaredScope =
            when (term) {
                is OtpErlangList -> toMacroStringDeclaredScope(term, scope, open, separator, close)
                else -> unknown()
            }
}
