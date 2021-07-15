package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo

object Union {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE) { toMacroString(type) }

    private const val SUBTYPE = "union"

    private fun toMacroString(type: OtpErlangTuple) =
        toOperands(type)
                .let { Sequence.toMacroStringDeclaredScope(it, Scope.EMPTY, "(", " | ", ")") }
                .macroString

    private fun toOperands(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
