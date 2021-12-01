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
    fun ifToMacroString(type: OtpErlangTuple): MacroString? = ifSubtypeTo(type, SUBTYPE) { toMacroString(type) }

    private const val SUBTYPE = "field_type"

    private fun toMacroString(@Suppress("UNUSED_PARAMETER") fieldType: OtpErlangTuple): MacroString =
        toPair(fieldType)
                ?.let { pairToMacroString(it) }
                ?: "missing_pair"

    private fun toPair(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(3)

    private fun pairToMacroString(pair: OtpErlangObject): MacroString =
            when (pair) {
                is OtpErlangList -> pairToMacroString(pair)
                else -> "unknown_pair"
            }

    private fun pairToMacroString(pair: OtpErlangList): MacroString {
        val fieldMacroString = fieldMacroString(pair)
        val typeMacroString = typeMacroString(pair)

        return "$fieldMacroString :: $typeMacroString"
    }

    private fun fieldMacroString(pair: OtpErlangList) =
            pairToField(pair)
                    ?.let { fieldToMacroString(it) }
                    ?: "missing_field"

    private fun pairToField(pair: OtpErlangList): OtpErlangObject? = pair.elementAt(0)

    private fun fieldToMacroString(field: OtpErlangObject) =
            Atom.toElixirAtom(field)?.let { elixirAtom ->
                Identifier.inspectAsFunction(elixirAtom, true)
            } ?: "unknown_field"

    private fun typeMacroString(pair: OtpErlangList) =
            pairToType(pair)
                    ?.let { typeToMacroString(it) }
                    ?: "missing_type"

    private fun pairToType(pair: OtpErlangList) = pair.elementAt(1)

    private fun typeToMacroString(type: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(type, Scope.EMPTY).macroString
}
