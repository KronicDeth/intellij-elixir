package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.NameArity
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attributes

private const val ATTRIBUTE_NAME = "type"

class Type(val attributes: Attributes, attribute: Attribute): MacroString(attribute) {
    val name by lazy { ((attribute.value as? OtpErlangTuple)?.elementAt(0) as? OtpErlangAtom)?.atomValue() }
    val value by lazy { ((attribute.value as? OtpErlangTuple)?.elementAt(1) as? OtpErlangTuple) }

    override fun toMacroString(): String =
        // TODO figure out how non-0 arity is represented
        name?.let { name ->
            val nameArity = NameArity(name, 0)

            val elixirAttributeName = if (attributes.exportTypeNameAritySet.contains(nameArity)) {
                "type"
            } else {
                "typep"
            }

            "@$elixirAttributeName $name :: ${valueMacroString()}"
        } ?: "?"

    private fun valueMacroString(): String = value?.let {
        org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type.toMacroString(it)
    } ?: "..."

    companion object {
        fun from(attributes: Attributes, attribute: Attribute): Type? =
                if (attribute.name == ATTRIBUTE_NAME) {
                    Type(attributes, attribute)
                } else {
                    null
                }
    }
}
