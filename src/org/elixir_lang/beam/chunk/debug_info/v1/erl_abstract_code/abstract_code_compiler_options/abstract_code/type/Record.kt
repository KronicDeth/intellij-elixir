package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.record.NameRecordFields

object Record {
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE) { toMacroString(type) }

    private const val SUBTYPE = "record"

    private fun toMacroString(type: OtpErlangTuple) =
            toNameRecordFields(type)
                    ?.let { NameRecordFields.toMacroString(it) }
                    ?: "missing_name_record_fields"

    private fun toNameRecordFields(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
