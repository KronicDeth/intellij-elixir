package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode.ifTag
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.*
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Fun
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.MapFieldAssociation
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.Record

object Type {
    fun <T> ifSubtypeTo(type: OtpErlangTuple, subtype: String, ifTrue: (OtpErlangTuple) -> T): T? =
            ifSubtypeTo(type, setOf(subtype), ifTrue)

    fun <T> ifSubtypeTo(type: OtpErlangTuple, subtypeSet: Set<String>, ifTrue: (OtpErlangTuple) -> T): T? =
        if (subtypeSet.contains(subtypeString(type))) {
            ifTrue(type)
        } else {
            null
        }

    fun <T> ifTo(term: OtpErlangObject, ifTrue: (OtpErlangTuple) -> T): T? = ifTag(term, TAG, ifTrue)

    fun ifToMacroStringDeclaredScope(
            term: OtpErlangObject
    ): MacroStringDeclaredScope? =
            ifTo(term) { toMacroStringDeclaredScope(it) }

    fun subtypeString(type: OtpErlangTuple): String =
            toSubtype(type)
                    ?.let { subtypeToString(it) }
                    ?: "missing_subtype"

    private const val TAG = "type"

    private fun subtypeToString(subtype: OtpErlangAtom) = subtype.atomValue()

    private fun subtypeToString(subtype: OtpErlangObject): String =
            when (subtype) {
                is OtpErlangAtom -> subtypeToString(subtype)
                else -> "unknown_subtype"
            }

    private fun toString(type: OtpErlangTuple): String =
        BoundedFun.ifToString(type) ?:
        Builtin.ifToString(type) ?:
        Constraint.ifToString(type) ?:
        FieldType.ifToString(type) ?:
        MapFieldAssociation.ifToString(type) ?:
        MapFieldExact.ifToString(type) ?:
        Fun.ifToString(type) ?:
        Product.ifToString(type) ?:
        Range.ifToString(type) ?:
        Record.ifToString(type) ?:
        Union.ifToString(type) ?:
        "unknown_type"

    private fun toMacroStringDeclaredScope(type: OtpErlangTuple) =
            MacroStringDeclaredScope(toString(type), doBlock = false, Scope.EMPTY)

    private fun toSubtype(type: OtpErlangTuple): OtpErlangObject? = type.elementAt(2)
}

