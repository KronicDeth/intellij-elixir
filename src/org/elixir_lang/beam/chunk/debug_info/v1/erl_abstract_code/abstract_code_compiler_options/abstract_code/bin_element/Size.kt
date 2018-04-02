package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object Size {
    fun isDefault(term: OtpErlangObject): Boolean = (term as OtpErlangAtom?)?.atomValue() == default

    fun toMacroString(term: OtpErlangAtom): String? =
            if (term.atomValue() == default) {
                null
            } else {
                "unknown_size_atom"
            }

    fun toMacroString(term: OtpErlangObject): String? =
            when (term) {
                is OtpErlangAtom -> toMacroString(term)
                else -> AbstractCode.toMacroStringDeclaredScope(term, Scope.EMPTY).macroString
            }

    private const val default = "default"
}
