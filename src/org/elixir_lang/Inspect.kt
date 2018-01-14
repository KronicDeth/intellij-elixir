package org.elixir_lang

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

object Inspect {
    object List {
        // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/inspect.ex#L199-L208
        tailrec fun isKeyword(term: OtpErlangObject): Boolean =
                if (term is OtpErlangList) {
                    if (term.arity() == 0) {
                        true
                    } else {
                        val head = term.head

                        if (head is OtpErlangTuple && head.arity() == 2) {
                            val key = head.elementAt(0)

                            if (key is OtpErlangAtom) {
                                if (key.atomValue().startsWith("Elixir.")) {
                                    false
                                } else {
                                    isKeyword(term.tail)
                                }
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
}
