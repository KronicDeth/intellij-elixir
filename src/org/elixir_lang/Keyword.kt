package org.elixir_lang

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

object Keyword {
    fun get(keyword: OtpErlangList, key: String): OtpErlangObject? =
        keyword.find { element ->
            val tuple = element as OtpErlangTuple

            assert(tuple.arity() == 2)

            (tuple.elementAt(0) as OtpErlangAtom).atomValue() == key
        }?.let { element ->
            (element as OtpErlangTuple).elementAt(1)
        }

    tailrec fun isKeyword(term: OtpErlangObject): Boolean =
            if (term is OtpErlangList) {
                if (term.arity() == 0) {
                    true
                } else {
                    val head = term.head

                    if (head is OtpErlangTuple && head.arity() == 2) {
                        val key = head.elementAt(0)

                        if (key is OtpErlangAtom) {
                            isKeyword(term.tail)
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                }
            } else {
                false
            }
}
