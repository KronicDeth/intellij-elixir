package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.ifSubtypeTo
import org.elixir_lang.code.Identifier

object FieldType {
    fun ifToString(type: OtpErlangTuple): String? = ifSubtypeTo(type, SUBTYPE) { toString(type) }

    private const val SUBTYPE = "field_type"

    private fun toString(fieldType: OtpErlangTuple): String =
        toPair(fieldType)
                ?.let { pairToString(it) }
                ?: AbstractCode.missing("pair", "${SUBTYPE} pair", fieldType)

    private fun toPair(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun pairToString(pair: OtpErlangObject): String =
            when (pair) {
                is OtpErlangList -> pairToString(pair)
                else -> AbstractCode.unknown("pair", "${SUBTYPE} pair", pair)
            }

    private fun pairToString(pair: OtpErlangList): String {
        val fieldString = fieldString(pair)
        val typeString = typeString(pair)

        return "$fieldString :: $typeString"
    }

    private fun fieldString(pair: OtpErlangList) =
            pairToField(pair)
                    ?.let { fieldToString(it) }
                    ?: AbstractCode.missing("field", "${SUBTYPE} field", pair)

    private fun pairToField(pair: OtpErlangList): OtpErlangObject? = pair.elementAt(0)

    private fun fieldToString(field: OtpErlangObject) =
            Atom.toElixirAtom(field)?.let { elixirAtom ->
                Identifier.inspectAsFunction(elixirAtom, true)
            } ?:
            AbstractCode.unknown("field", "${SUBTYPE} field", field)

    private fun typeString(pair: OtpErlangList) =
            pairToType(pair)
                    ?.let { typeToString(it) }
                    ?: AbstractCode.missing("type", "${SUBTYPE} type", pair)

    private fun pairToType(pair: OtpErlangList) = pair.elementAt(1)

    private fun typeToString(type: OtpErlangObject) = AbstractCode.toString(type)
}
