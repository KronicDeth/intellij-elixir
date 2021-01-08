package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo

object FieldType {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE) { toMacroString(type) }

    private const val SUBTYPE = "field_type"

    private fun toMacroString(@Suppress("UNUSED_PARAMETER") type: OtpErlangTuple): MacroString {
        TODO()
    }
}
