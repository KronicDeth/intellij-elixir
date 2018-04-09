package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.Value

private const val ATTRIBUTE_NAME = "spec"

class Spec(attribute: Attribute): MacroString(attribute) {
    val name by lazy { nameArity?.elementAt(0)?.let { Value.nameToMacroString(it) } }
    val arity by lazy { nameArity?.elementAt(1)?.let { Value.arityToBigInteger(it) } }

    override fun toMacroString(): String =
        valueMacroStrings().joinToString("\n") { valueMacroString ->
            "@spec $valueMacroString"
        }

    private val nameArity by lazy {
        (attribute.value as? OtpErlangTuple)?.let { Value.toNameArity(it) } as? OtpErlangTuple
    }

    private fun valueMacroStrings() =
            attribute.value
                    ?.let { Value.toMacroStrings(it) }
                    ?: emptyList()

    companion object {
        fun from(attribute: Attribute): Spec? =
                if (attribute.name == ATTRIBUTE_NAME) {
                    Spec(attribute)
                } else {
                    null
                }
    }
}
