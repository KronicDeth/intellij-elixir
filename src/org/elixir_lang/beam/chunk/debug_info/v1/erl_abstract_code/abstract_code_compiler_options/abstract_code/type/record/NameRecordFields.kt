package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.record

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Record as AbstractCodeRecord

object NameRecordFields {
    fun toMacroString(term: OtpErlangObject): MacroString =
            when (term) {
                is OtpErlangList -> toMacroString(term)
                else -> "unknown_name_record_fields"
            }

    private fun nameMacroString(nameRecordFields: OtpErlangList) =
            toName(nameRecordFields)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

    private fun nameToMacroString(name: OtpErlangObject) =
            Atom.toElixirAtom(name)?.let { AbstractCodeRecord.nameToMacroString(it) }

    private fun recordFieldsMacroString(nameRecordFields: OtpErlangList) =
            toRecordFields(nameRecordFields).let { recordFieldsToMacroString(it) }

    private fun recordFieldsToMacroString(recordFields: List<OtpErlangObject>) =
            recordFields.joinToString(", ") {
                AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString
            }

    private fun toMacroString(nameRecordFields: OtpErlangList): MacroString {
        val nameMacroString = nameMacroString(nameRecordFields)
        val recordFieldsMacroString = recordFieldsMacroString(nameRecordFields)

        return "$nameMacroString($recordFieldsMacroString)"
    }

    private fun toName(nameRecordFields: OtpErlangList): OtpErlangObject? = nameRecordFields.elementAt(0)
    private fun toRecordFields(nameRecordFields: OtpErlangList): List<OtpErlangObject> = nameRecordFields.elements().drop(1)
}
