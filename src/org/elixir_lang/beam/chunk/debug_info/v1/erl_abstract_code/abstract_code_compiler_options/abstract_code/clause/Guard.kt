package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.clause

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Separator
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence

object Guard {
    fun toMacroString(term: OtpErlangList): String =
            Sequence
                    .toMacroStringDeclaredScope(term, Scope.EMPTY, "", Separator(" and ", group = true), "")
                    .macroString
                    .string

    fun toMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> toMacroString(term)
                else -> AbstractCode.unknown("guard", "clause guard", term)
            }
}
