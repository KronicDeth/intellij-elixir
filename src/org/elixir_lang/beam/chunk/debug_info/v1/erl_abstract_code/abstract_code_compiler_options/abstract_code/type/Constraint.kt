package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.constraint.IsSubtype

object Constraint {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE) { toMacroString(type) }

    private const val SUBTYPE = "constraint"

    private fun guardArgumentsToMacroString(guardArguments: OtpErlangObject) =
            IsSubtype.ifToMacroString(guardArguments)
                    ?: "missing_var: missing_type"

    private fun toMacroString(type: OtpErlangTuple)=
            toGuardArguments(type)
                    ?.let { guardArgumentsToMacroString(it) }
                    ?: "missing_var: missing_type"

    private fun toGuardArguments(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
