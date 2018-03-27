package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.chunk.component1
import org.elixir_lang.beam.chunk.component2

private const val TAG = "bin_element"

class BinElement(private val term: OtpErlangTuple): Node(term) {
    val pattern by lazy {
        (term.elementAt(2) as? OtpErlangTuple)?.let { Node.from(it) }
    }
    val size by lazy {
        term.elementAt(3)?.let { element ->
            when (element) {
                is OtpErlangAtom -> element
                else -> Node.from(element)
            }
        }
    }
    val typeSpecifierList by lazy {
        term.elementAt(4)?.let { element ->
            when (element) {
                is OtpErlangAtom -> element
                is OtpErlangList -> element.toList()
                else -> TODO()
            }
        }
    }

    override fun toMacroString(): String =
        arrayOf(pattern?.toMacroString(), optionsToMacroString()).filterNotNull().joinToString(" :: ")

    private fun optionsToMacroString(): String? {
        val nonNullOptionMacroStrings =
                arrayOf(typeSpecifierListToMacroString(), sizeToMacroString()).filterNotNull()

        return if (nonNullOptionMacroStrings.isEmpty()) {
            null
        } else {
            nonNullOptionMacroStrings.joinToString("-")
        }
    }

    private fun sizeToMacroString(): String? =
        when (size) {
            is OtpErlangAtom ->
                if ((size as OtpErlangAtom).atomValue() == "default") {
                    null
                } else {
                    "?"
                }
            is Node -> (size as Node).toMacroString()
            else -> "?"
        }

    private fun typeSpecifierListToMacroString(): String? =
            when (typeSpecifierList) {
                is OtpErlangAtom ->
                    if ((typeSpecifierList as OtpErlangAtom).atomValue() == "default") {
                        null
                    } else {
                        "?"
                    }
                is List<*> -> (typeSpecifierList as List<*>).joinToString("-") {
                    when (it) {
                        is OtpErlangAtom -> it.atomValue()
                        is OtpErlangTuple ->
                                if (it.arity() == 2) {
                                    val (name, value) = it

                                    if (name is OtpErlangAtom && value is OtpErlangLong) {
                                        "${name.atomValue()}(${value.longValue()})"
                                    } else {
                                        TODO()
                                    }
                                } else {
                                    TODO()
                                }
                        else -> TODO()
                    }
                }
                else -> "?"
            }

    companion object {
        fun from(form: OtpErlangTuple): BinElement? =
                (form.elementAt(0) as? OtpErlangAtom)?.let { tag ->
                    if (tag.atomValue() == TAG) {
                        BinElement(form)
                    } else {
                        null
                    }
                }
    }
}
