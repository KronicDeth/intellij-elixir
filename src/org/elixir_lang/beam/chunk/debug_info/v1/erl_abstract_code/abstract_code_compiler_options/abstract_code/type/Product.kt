package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.COMMA
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo

object Product {
    fun <T> ifTo(type: OtpErlangTuple, ifTrue: (OtpErlangTuple) -> T): T? = ifSubtypeTo(type, SUBTYPE, ifTrue)
    fun ifToString(type: OtpErlangTuple): String? = ifTo(type) { toString(type) }

    private const val SUBTYPE = "product"

    private fun toString(type: OtpErlangTuple) =
        toOperands(type)
                .let { Sequence.toMacroStringDeclaredScope(it, Scope.EMPTY, "", COMMA, "") }
                .macroString
                .string

    fun toOperands(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
