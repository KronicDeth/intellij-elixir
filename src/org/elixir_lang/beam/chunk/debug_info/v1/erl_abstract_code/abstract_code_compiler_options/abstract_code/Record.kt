package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.code.Identifier.inspectAsFunction

object Record {
    fun ifToMacroString(term: OtpErlangObject?): String? = AbstractCode.ifTag(term, TAG) { toMacroString(it) }

    fun toMacroString(term: OtpErlangTuple): String {
        val arity = term.arity()

        return when (arity) {
            4 -> creationToMacroString(term)
            5 -> updateToMacroString(term)
            else -> "unknown_record_arity"
        }
    }

    private const val TAG = "record"

    private fun creationNameMacroString(term: OtpErlangTuple): String =
            toCreationName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_creation_name"

    private fun creationToMacroString(term: OtpErlangTuple): String {
        val nameMacroString = creationNameMacroString(term)
        val recordFieldsMacroString = creationRecordFieldsMacroString(term)

        return "$nameMacroString($recordFieldsMacroString)"
    }

    private fun creationRecordFieldsMacroString(term: OtpErlangTuple): String =
            toCreationRecordFields(term)
                    ?.let { recordFieldsToMacroString(it) }
                    ?: "missing_record_fields"

    private fun nameToMacroString(term: OtpErlangObject): String =
            when (term) {
                is OtpErlangAtom -> inspectAsFunction(term)
                else -> "unknown_record_name"
            }

    private fun recordFieldsToMacroString(recordFields: OtpErlangList): String =
            recordFields.joinToString(", ") { AbstractCode.toMacroString(it) }

    private fun recordFieldsToMacroString(recordFields: OtpErlangObject): String =
            when (recordFields) {
                is OtpErlangList -> recordFieldsToMacroString(recordFields)
                else -> "unknown_record_fields"
            }

    private fun toCreationName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toCreationRecordFields(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toUpdatable(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(2)
    private fun toUpdateName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(3)
    private fun toUpdateRecordFields(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(4)

    private fun updatableMacroString(term: OtpErlangTuple): String =
            toUpdatable(term)
                    ?.let { AbstractCode.toMacroString(it) }
                    ?: "unknown_updatable"

    private fun updateNameMacroString(term: OtpErlangTuple): String =
            toUpdateName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_update_name"

    private fun updateRecordFieldsMacroString(term: OtpErlangTuple): String =
            toUpdateRecordFields(term)
                    ?.let { recordFieldsToMacroString(it) }
                    ?: "missing_record_fields"

    private fun updateToMacroString(term: OtpErlangTuple): String {
        val updatableMacroString = updatableMacroString(term)
        val nameMacroString = updateNameMacroString(term)
        val recordFieldsMacroString = updateRecordFieldsMacroString(term)

        return "$nameMacroString($updatableMacroString, $recordFieldsMacroString)"
    }
}
