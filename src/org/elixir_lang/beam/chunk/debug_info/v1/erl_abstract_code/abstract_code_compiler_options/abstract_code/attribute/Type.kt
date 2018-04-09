package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.NameArity
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attributes
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope

private const val ATTRIBUTE_NAME = "type"

class Type(val attributes: Attributes, attribute: Attribute): MacroString(attribute) {
    val elixirAttributeName by lazy { elixirAttributeName() }
    val name by lazy { ((attribute.value as? OtpErlangTuple)?.elementAt(0) as? OtpErlangAtom)?.atomValue() }
    val value by lazy { ((attribute.value as? OtpErlangTuple)?.elementAt(1) as? OtpErlangTuple) }

    override fun toMacroString(): String {
        val elixirAttributeName = elixirAttributeName()
        val subtypeMacroString = subtypeMacroString()

        return "@$elixirAttributeName $subtypeMacroString"
    }

    private fun elixirAttributeName() =
        name?.let {
            nameToElixirAttributeName(it)
        } ?:
        "unknown_type_attribute"

    private fun nameArityToElixirAttributeName(nameArity: NameArity) =
            if (attributes.exportTypeNameAritySet.contains(nameArity)) {
                "type"
            } else {
                "typep"
            }

    private fun nameToElixirAttributeName(name: String) =
            NameArity(name, 0).let { nameArityToElixirAttributeName(it) }

    private fun subtypeMacroString() = subtypeMacroString(attribute.value)

    companion object {
        fun from(attributes: Attributes, attribute: Attribute): Type? =
                if (attribute.name == ATTRIBUTE_NAME) {
                    Type(attributes, attribute)
                } else {
                    null
                }

        private fun nameMacroString(term: OtpErlangTuple) =
            toName(term)
                    ?.let { nameToMacroString(it) }
                    ?: "missing_name"

        private fun nameToMacroString(name: OtpErlangObject): String =
                when (name) {
                    is OtpErlangAtom -> name.atomValue()
                    else -> "unknown_name"
                }

        private fun subtypeMacroString(term: OtpErlangObject?) =
                when (term) {
                    is OtpErlangTuple -> subtypeMacroString(term)
                    else -> "unknown_subtype"
                }

        private fun subtypeMacroString(term: OtpErlangTuple): String {
            val nameMacroString = nameMacroString(term)
            val valueMacroString = valueMacroString(term)

            return "$nameMacroString :: $valueMacroString"
        }

        private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(0)
        private fun toValue(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(1)

        private fun valueMacroString(term: OtpErlangTuple) =
                toValue(term)
                        ?.let { valueToMacroString(it) }
                        ?: "unknown_value"

        private fun valueToMacroString(value: OtpErlangObject) =
                AbstractCode.toMacroStringDeclaredScope(value, Scope.EMPTY).macroString
    }
}
