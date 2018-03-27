package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.attribute

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.NameArity
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Attribute
import org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code.Type

private const val ATTRIBUTE_NAME = "spec"

class Spec(attribute: Attribute): MacroString(attribute) {
    val nameArity by lazy { (attribute.value as? OtpErlangTuple)?.elementAt(0)?.let { NameArity.from(it) }}
    private val argumentsReturnsList by lazy {
        ((attribute.value as? OtpErlangTuple)?.elementAt(1) as? OtpErlangList)?.let { typeList ->
            if (typeList.arity() == 1) {
                (typeList.elementAt(0) as? OtpErlangTuple)?.let { type ->
                    if ((type.elementAt(0) as? OtpErlangAtom)?.atomValue() == "type") {
                        if ((type.elementAt(2) as? OtpErlangAtom)?.atomValue() == "fun") {
                            (type.elementAt(3) as? OtpErlangList)?.let { argumentsReturnList ->
                                if (argumentsReturnList.arity() == 2) {
                                    argumentsReturnList
                                } else {
                                    null
                                }
                            }
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        }
    }
    private val arguments by lazy { argumentsReturnsList?.elementAt(0) }
    private val returns by lazy { argumentsReturnsList?.elementAt(1) }

    override fun toMacroString(): String =
        "@spec ${nameMacroString()}(${argumentsMacroString()}) :: ${returnsMacroString()}"

    private fun argumentsMacroString(): String = arguments?.let { Type.toMacroString(it) } ?: "..."
    private fun nameMacroString(): String = nameArity?.name ?: "?"
    private fun returnsMacroString(): String = returns?.let { Type.toMacroString(it) } ?: "..."

    companion object {
        fun from(attribute: Attribute): Spec? =
                if (attribute.name == ATTRIBUTE_NAME) {
                    Spec(attribute)
                } else {
                    null
                }
    }
}
