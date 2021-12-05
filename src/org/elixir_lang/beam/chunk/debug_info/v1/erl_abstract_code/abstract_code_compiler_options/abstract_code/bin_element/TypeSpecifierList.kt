package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object TypeSpecifierList {
    fun isDefault(term: OtpErlangObject): Boolean = (term as? OtpErlangAtom)?.atomValue() == "default"

    fun toMacroString(term: OtpErlangAtom): String? =
            if (term.atomValue() == "default") {
                null
            } else {
                AbstractCode.unknown("type_specifier_list_atom", "Bin element type specifier list atom", term)
            }

    fun toMacroString(term: OtpErlangList): String = term.joinToString("-") { TypeSpecifier.toMacroString(it) }

    fun toMacroString(term: OtpErlangObject): String? =
            when (term) {
                is OtpErlangAtom -> toMacroString(term)
                is OtpErlangList -> toMacroString(term)
                else -> AbstractCode.unknown("type_specifier_list", "Bin element type specifier list", term)
            }
}
