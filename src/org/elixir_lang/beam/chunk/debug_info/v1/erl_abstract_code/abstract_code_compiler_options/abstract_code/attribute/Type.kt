package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.NameArity
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.AbstractCode
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attributes
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Scope
import org.elixir_lang.beam.decompiler.Options
import org.elixir_lang.code.Identifier

private const val ATTRIBUTE_NAME = "type"

class Type(val attributes: Attributes, attribute: Attribute): MacroString(attribute) {
    val elixirAttributeName by lazy { elixirAttributeName() }
    val name by lazy { ((attribute.value as? OtpErlangTuple)?.elementAt(0) as? OtpErlangAtom)?.atomValue() }
    val value by lazy { ((attribute.value as? OtpErlangTuple)?.elementAt(1) as? OtpErlangTuple) }

    override fun toMacroString(options: Options): String {
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

        private fun nameString(term: OtpErlangTuple): String =
            toName(term)
                    ?.let { nameToMacroString(it) }
                    ?: AbstractCode.missing("name", "Attribute type name", term)

        private fun nameToMacroString(name: OtpErlangObject): String =
                when (name) {
                    is OtpErlangAtom -> Identifier.inspectAsFunction(name, true)
                    else -> AbstractCode.unknown("name", "Attribute type name", name)
                }

        private fun subtypeMacroString(term: OtpErlangObject?) =
                when (term) {
                    is OtpErlangTuple -> subtypeMacroString(term)
                    else -> if (term != null) {
                        AbstractCode.unknown("subtype", "Attribute type subtype", term)
                    } else {
                        "missing_subtype"
                    }
                }

        private fun subtypeMacroString(term: OtpErlangTuple): String {
            val nameString = nameString(term)
            val valueString = valueString(term)

            return "$nameString :: $valueString"
        }

        private fun toName(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(0)
        private fun toValue(term: OtpErlangTuple): OtpErlangObject? = term.elementAt(1)

        private fun valueString(term: OtpErlangTuple): String =
                toValue(term)
                        ?.let { valueToString(it) }
                        ?: AbstractCode.unknown("value", "Attribute type value", term)

        private fun valueToString(value: OtpErlangObject): String = AbstractCode.toString(value)
    }
}
