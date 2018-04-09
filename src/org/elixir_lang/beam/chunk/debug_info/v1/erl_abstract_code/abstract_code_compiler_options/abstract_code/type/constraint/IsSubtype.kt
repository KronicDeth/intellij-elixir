package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.type.constraint

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Atom
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Var

object IsSubtype {
    fun ifToMacroString(guardArguments: OtpErlangObject): MacroString? = ifTo(guardArguments) { toMacroString(it) }

    private fun argumentsToMacroString(arguments: OtpErlangList): MacroString {
        val varMacroString = varMacroString(arguments)
        val typeMacroString = typeMacroString(arguments)

        return "$varMacroString $typeMacroString"
    }

    private fun argumentsToMacroString(arguments: OtpErlangObject) =
            when (arguments) {
                is OtpErlangList -> argumentsToMacroString(arguments)
                else -> "unknown_arguments_var: unknown_arguments_type"
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

    private fun toMacroString(isSubtype: OtpErlangList) =
            toArguments(isSubtype)
                    ?.let { argumentsToMacroString(it) }
                    ?: "is_subtype_missing_arguments_var: is_substype_missing_arguments_type"

    private fun typeMacroString(arguments: OtpErlangList) =
            argumentsToType(arguments)
                    ?.let { typeToMacroString(it) }
                    ?: "missing_type"

    private fun typeToMacroString(type: OtpErlangObject) =
            AbstractCode.toMacroStringDeclaredScope(type, Scope.EMPTY).macroString

    private fun varMacroString(arguments: OtpErlangList) =
            argumentsToVar(arguments)
                    ?.let { varToMacroString(it) }
                    ?: "missing_var:"

    private fun varToMacroString(`var`: OtpErlangObject) =
            Var.ifToKey(`var`)
            ?: "unknown_var:"
}
