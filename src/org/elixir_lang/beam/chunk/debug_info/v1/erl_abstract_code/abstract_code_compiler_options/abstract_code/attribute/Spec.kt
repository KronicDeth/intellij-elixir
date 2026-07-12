package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.anonymousVariableToAny
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute.spec.Value
import org.elixir_lang.beam.decompiler.Options

private const val ATTRIBUTE_NAME = "spec"

class Spec(attribute: Attribute): MacroString(attribute) {
    val name by lazy { nameArity?.elementAt(0)?.let { Value.nameToString(it) } }
    val arity by lazy { nameArity?.elementAt(1)?.let { Value.arityToBigInteger(it) } }

    override fun toMacroString(options: Options): String =
        valueMacroStrings().joinToString("\n") { valueMacroString ->
            "@spec $valueMacroString"
        }

    private val nameArity by lazy {
        (attribute.value as? OtpErlangTuple)?.let { Value.toNameArity(it) } as? OtpErlangTuple
    }

    // A spec value holds only the function name/arity (atoms/integers) and type clauses, so rewriting anonymous
    // `_` variables to `any()` over the whole term only affects the types (see [anonymousVariableToAny]); the
    // name/arity used elsewhere is read from the original `attribute.value` and is unaffected.
    private fun valueMacroStrings() =
            attribute.value
                    ?.let { anonymousVariableToAny(it) }
                    ?.let { Value.toStrings(it) }
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
