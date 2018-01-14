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
}
