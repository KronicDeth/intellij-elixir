package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec

import com.ericsson.otp.erlang.*
import org.elixir_lang.Macro.ifTupleTo
import org.elixir_lang.beam.Decompiler
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.MacroString
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.value.Definition
import org.elixir_lang.beam.decompiler.Default
import org.elixir_lang.beam.decompiler.MacroNameArity
import org.elixir_lang.code.Identifier.inspectAsFunction
import org.elixir_lang.psi.call.name.Function.DEF
import java.math.BigInteger

object Value {
    fun toMacroStrings(value: OtpErlangObject): List<MacroString> =
            when (value) {
                is OtpErlangTuple -> toMacroStrings(value)
                else -> emptyList()
            }

    internal fun arityToBigInteger(arity: OtpErlangObject): BigInteger =
            when (arity) {
                is OtpErlangLong -> arity.bigIntegerValue()
                else -> BigInteger("-1")
            }

    internal fun nameToMacroString(name: OtpErlangObject): MacroString =
            when (name) {
                is OtpErlangAtom -> nameToMacroString(name)
                else -> "unknown_name"
            }

    private fun toMacroNameArity(value: OtpErlangTuple): org.elixir_lang.beam.MacroNameArity? =
         toNameArity(value)?.let { nameArityTerm ->
            ifTupleTo(nameArityTerm, 2) { tuple ->
                tuple.elementAt(0).let { it as? OtpErlangAtom }?.atomValue()?.let { name ->
                    tuple.elementAt(1)?.let { it as? OtpErlangLong }?.intValue()?.let { arity ->
                        org.elixir_lang.beam.MacroNameArity(DEF, name, arity)
                    }
                }
            }
        }

    internal fun toNameArity(value: OtpErlangTuple): OtpErlangObject? = value.elementAt(0)

    private fun nameToMacroString(name: OtpErlangAtom) = inspectAsFunction(name)

    private fun toDefinitions(value: OtpErlangTuple): OtpErlangObject? = value.elementAt(1)

    private fun toMacroStrings(value: OtpErlangTuple) =
            toMacroNameArity(value)
                    ?.let { macroNameArity ->
                        val decompiler = Decompiler.decompiler(macroNameArity) ?: Default.INSTANCE

                        toDefinitions(value)
                                ?.let { definitionsToMacroStrings(it, decompiler, macroNameArity) }
                    }
                    .orEmpty()

    private fun definitionsToMacroStrings(definitions: OtpErlangObject, decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity) =
            when (definitions) {
                is OtpErlangList -> definitionsToMacroStrings(definitions, decompiler, macroNameArity)
                else -> emptyList()
            }

    private fun definitionsToMacroStrings(definitions: OtpErlangList, decompiler: MacroNameArity, macroNameArity: org.elixir_lang.beam.MacroNameArity) =
            definitions.map {
                Definition.toMacroString(it, decompiler, macroNameArity)
            }
}
