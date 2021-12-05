package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.bin_element

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.component1
import org.elixir_lang.beam.chunk.component2
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode

object TypeSpecifier {
    fun toMacroString(term: OtpErlangAtom): String = term.atomValue()

    fun toMacroString(term: OtpErlangTuple): String =
            if (term.arity() == 2) {
                val (name, value) = term

                if (name is OtpErlangAtom && value is OtpErlangLong) {
                    "${name.atomValue()}(${value.longValue()})"
                } else {
                    AbstractCode.unknown("type_specifier_name_value_pair", "Bin element type specifier pair", term)
                }
            } else {
                AbstractCode.unknown("type_specifier_tuple", "Bin element type specifier tuple", term)
            }

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> toMacroString(term)
                is OtpErlangTuple -> toMacroString(term)
                else -> AbstractCode.unknown("type_specifier", "Bin element type specifier", term)
            }
}
