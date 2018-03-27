package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.term.inspect

private const val TAG = "attribute"

open class Attribute(form: OtpErlangTuple) {
    val line by lazy { (form.elementAt(1) as? OtpErlangLong)?.longValue() }
    val name by lazy { (form.elementAt(2) as? OtpErlangAtom)?.atomValue() }
    val value: OtpErlangObject? by lazy { form.elementAt(3) }

    fun toMacroString() = "@$name ${value?.let { inspect(it) } ?: "?"}"

    companion object {
        fun from(form: OtpErlangTuple): Attribute? =
                (form.elementAt(0) as? OtpErlangAtom)?.let { tag ->
                    if (tag.atomValue() == TAG)  {
                        Attribute(form)
                    } else {
                        null
                    }
                }
    }
}
