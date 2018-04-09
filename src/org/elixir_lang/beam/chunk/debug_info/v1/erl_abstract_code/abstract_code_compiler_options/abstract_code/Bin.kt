package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.term.inspect


object Bin {
    fun ifBinElementsToMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope? =
        toBinElements(term)?.let { binElements ->
            BinElements.toElixirString(binElements)?.let {
                inspect(it)
                        .let { MacroStringDeclaredScope(it, Scope.EMPTY) }
            } ?:
            BinElements.toMacroStringDeclaredScope(binElements, scope)
        }

    fun <T> ifTo(term: OtpErlangObject?, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it, scope) }

    fun toMacroStringDeclaredScope(term: OtpErlangTuple, scope: Scope): MacroStringDeclaredScope {
        val binElements = toBinElements(term)

        return if (binElements != null) {
            binElementsElixirStringToMacroStringDeclaredScope(binElements)
                    ?: binElementsToMacroStringDeclaredScope(binElements, scope)
        } else {
            MacroStringDeclaredScope("missing_bin_elements", Scope.EMPTY)
        }
    }

    private const val TAG = "bin"

    private fun binElementsToMacroStringDeclaredScope(binElements: OtpErlangObject, scope: Scope) =
        BinElements
                .toMacroStringDeclaredScope(binElements, scope)
                .let { (binElementsMacroString, binElementsDeclaredScope) ->
                    MacroStringDeclaredScope("<<$binElementsMacroString>>", binElementsDeclaredScope)
                }

    private fun binElementsElixirStringToMacroStringDeclaredScope(binElements: OtpErlangObject) =
            BinElements
                    .toElixirString(binElements)
                    ?.let {
                        elixirStringToMacroStirngDeclaredScope(it)
                    }

    private fun elixirStringToMacroStirngDeclaredScope(elixirString: OtpErlangBinary) =
           inspect(elixirString)
                   .let { MacroStringDeclaredScope(it, Scope.EMPTY) }

    private fun toBinElements(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
}
