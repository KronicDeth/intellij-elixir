package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.compiler_options

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

val TRUE = OtpErlangAtom(true)

class PropertyList(val list: OtpErlangList) {
    operator fun get(index: Int): OtpErlangObject? = list.elementAt(index)?.let { expand(it) }

    operator fun get(key: String): OtpErlangObject? =
        list.find {
            when (it) {
                is OtpErlangAtom -> it.atomValue() == key
                is OtpErlangTuple ->
                    if (it.arity() == 2) {
                        (it.elementAt(0) as? OtpErlangAtom)?.atomValue() == key
                    } else {
                        false
                    }
                else -> false
            }
        }?.let {
            when (it) {
                is OtpErlangAtom -> TRUE
                is OtpErlangTuple -> it.elementAt(1)
                else -> null
            }
        }

    operator fun get(key: OtpErlangAtom): OtpErlangObject? = get(key.atomValue())

    fun size() = list.arity()

    companion object {
        fun from(term: OtpErlangObject): PropertyList? = (term as? OtpErlangList)?.let(::PropertyList)

        private fun expand(entry: OtpErlangObject) =
                when (entry) {
                    is OtpErlangAtom -> OtpErlangTuple(arrayOf(entry, TRUE))
                    is OtpErlangTuple ->
                            if (entry.arity() == 2 && entry.elementAt(0) is OtpErlangAtom) {
                                entry
                            } else {
                                null
                            }
                    else -> null
                }
    }
}
