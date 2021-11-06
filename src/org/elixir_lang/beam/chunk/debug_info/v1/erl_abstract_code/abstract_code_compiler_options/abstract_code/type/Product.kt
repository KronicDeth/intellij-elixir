package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo

object Product {
    fun <T> ifTo(type: OtpErlangTuple, ifTrue: (OtpErlangTuple) -> T): T? = ifSubtypeTo(type, SUBTYPE, ifTrue)
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifTo(type) { toMacroString(type) }

    private const val SUBTYPE = "product"

    private fun toMacroString(type: OtpErlangTuple) =
        toOperands(type)
                .let { Sequence.toMacroStringDeclaredScope(it, Scope.EMPTY, "", ", ", "") }
                .macroString

    fun toOperands(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
