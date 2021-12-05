package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.record

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Sequence
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Record as AbstractCodeRecord

object NameRecordFields {
    fun toString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangList -> toString(term)
                else -> AbstractCode.unknown("name_record_fields", "type record", term)
            }

    private fun nameString(nameRecordFields: OtpErlangList): String =
            toName(nameRecordFields)
                    ?.let { nameToString(it) }
                    ?: AbstractCode.missing("name", "type record name", nameRecordFields)

    private fun nameToString(name: OtpErlangObject) =
            Atom.toElixirAtom(name)?.let { AbstractCodeRecord.nameToString(it) }

    private fun recordFieldsString(nameRecordFields: OtpErlangList) =
            toRecordFields(nameRecordFields).let { recordFieldsToString(it) }

    private fun recordFieldsToString(recordFields: List<OtpErlangObject>): String =
            recordFields.joinToString(", ") {
                AbstractCode.toMacroStringDeclaredScope(it, Scope.EMPTY).macroString.group().string
            }

    private fun toString(nameRecordFields: OtpErlangList): String {
        val nameString = nameString(nameRecordFields)
        val recordFieldsString = recordFieldsString(nameRecordFields)

        return "$nameString($recordFieldsString)"
    }

    private fun toName(nameRecordFields: OtpErlangList): OtpErlangObject? = nameRecordFields.elementAt(0)
    private fun toRecordFields(nameRecordFields: OtpErlangList): List<OtpErlangObject> = nameRecordFields.elements().drop(1)
}
