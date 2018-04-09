package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject

object BinElements {
    fun toElixirString(term: OtpErlangObject): OtpErlangBinary? =
        singleOrNull(term)?.let { binElement ->
            BinElement.ifTo(binElement) { binElementTuple ->
                BinElement.toElixirString(binElementTuple)
            }
        }

    fun toMacroStringDeclaredScope(term: OtpErlangObject, scope: Scope): MacroStringDeclaredScope =
            Elements.toMacroStringDeclaredScope(term, scope)

    private fun singleOrNull(term: OtpErlangObject): OtpErlangObject? = (term as? OtpErlangList)?.singleOrNull()
}
