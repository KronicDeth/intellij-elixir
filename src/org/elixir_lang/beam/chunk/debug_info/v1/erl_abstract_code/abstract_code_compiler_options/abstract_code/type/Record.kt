package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.record.NameRecordFields

object Record {
    fun ifToString(type: OtpErlangTuple): String? = ifSubtypeTo(type, SUBTYPE) { toString(type) }

    private const val SUBTYPE = "record"

    private fun toString(type: OtpErlangTuple) =
            toNameRecordFields(type)
                    ?.let { NameRecordFields.toString(it) }
                    ?: AbstractCode.missing("name_record_fields", "type ${SUBTYPE} name and record fields", type)

    private fun toNameRecordFields(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)
}
