package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

object Size {
    fun isDefault(term: OtpErlangObject): Boolean = (term as? OtpErlangAtom?)?.atomValue() == default

    fun toString(term: OtpErlangAtom): String? =
            if (term.atomValue() == default) {
                null
            } else {
                AbstractCode.unknown("size_atom", "bin element size atom", term)
            }

    fun toString(term: OtpErlangObject): String? =
            when (term) {
                is OtpErlangAtom -> toString(term)
                else -> AbstractCode.toString(term)
            }

    private const val default = "default"
}
