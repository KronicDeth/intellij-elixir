package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.constraint

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Var

object IsSubtype {
    fun ifToString(guardArguments: OtpErlangObject): String? = ifTo(guardArguments) { toString(it) }

    private fun argumentsToString(arguments: OtpErlangList): String {
        val varString = varString(arguments)
        val typeString = typeString(arguments)

        return "$varString $typeString"
    }

    private fun argumentsToString(arguments: OtpErlangObject) =
            when (arguments) {
                is OtpErlangList -> argumentsToString(arguments)
                else -> AbstractCode.error("unknown_arguments_var: unknown_arguments_type", "type constraint subtype arguments", arguments)
            }

    private fun argumentsToType(arguments: OtpErlangList): OtpErlangObject? = arguments.elementAt(1)
    private fun argumentsToVar(arguments: OtpErlangList): OtpErlangObject? = arguments.elementAt(0)

    private fun <T> ifGuardTo(guard: OtpErlangObject, guardArguments: OtpErlangList, ifTrue: (OtpErlangList) -> T): T? =
            if (Atom.toElixirAtom(guard)?.atomValue() == "is_subtype") {
                ifTrue(guardArguments)
            } else {
                null
            }

    private fun <T> ifTo(guardArguments: OtpErlangList, ifTrue: (OtpErlangList) -> T): T? =
            toGuard(guardArguments)
                    ?.let { ifGuardTo(it, guardArguments, ifTrue) }

    private fun <T> ifTo(guardArguments: OtpErlangObject, ifTrue: (OtpErlangList) -> T): T? =
            when (guardArguments) {
                is OtpErlangList -> ifTo(guardArguments, ifTrue)
                else -> null
            }

    private fun toGuard(guardArguments: OtpErlangList): OtpErlangObject? = guardArguments.elementAt(0)
    private fun toArguments(isSubType: OtpErlangList): OtpErlangObject? = isSubType.elementAt(1)

    private fun toString(isSubtype: OtpErlangList) =
            toArguments(isSubtype)
                    ?.let { argumentsToString(it) }
                    ?: AbstractCode.error("is_subtype_missing_arguments_var: is_substype_missing_arguments_type", "type constraint subtype arguments are missing", isSubtype)

    private fun typeString(arguments: OtpErlangList) =
            argumentsToType(arguments)
                    ?.let { typeToString(it) }
                    ?: AbstractCode.missing("type", "type constraint subtype type", arguments)

    private fun typeToString(type: OtpErlangObject) = AbstractCode.toString(type)

    private fun varString(arguments: OtpErlangList) =
            argumentsToVar(arguments)
                    ?.let { varToString(it) }
                    ?: AbstractCode.error("missing_var:", "type constraint subtype var", arguments)

    private fun varToString(`var`: OtpErlangObject) =
            Var.ifToKey(`var`)
            ?: AbstractCode.error("unknown_var:", "type constraint subtype var", `var`)
}
