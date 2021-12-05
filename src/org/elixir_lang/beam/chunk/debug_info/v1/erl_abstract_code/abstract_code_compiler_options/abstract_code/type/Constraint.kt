package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.constraint.IsSubtype

object Constraint {
    fun ifToString(type: OtpErlangTuple): String? = ifSubtypeTo(type, SUBTYPE) { toString(type) }

    private const val SUBTYPE = "constraint"

    private fun guardArgumentsToString(guardArguments: OtpErlangObject) =
            IsSubtype.ifToString(guardArguments)
                    ?: AbstractCode.error("missing_var: missing_type", "type constraint guard arguments are missing", guardArguments)

    private fun toString(type: OtpErlangTuple) =
            toGuardArguments(type)
                    ?.let { guardArgumentsToString(it) }
                    ?: "missing_var: missing_type"

    private fun toGuardArguments(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
