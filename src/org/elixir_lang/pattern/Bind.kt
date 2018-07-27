package org.elixir_lang.pattern

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpOutputStream

class Bind(private val name: String) : OtpErlangObject() {
    override fun <T : Any?> match(term: OtpErlangObject, binds: T): Boolean =
            if (binds is Binds) {
                true
            } else {
                super.match(term, binds)
            }

    override fun equals(other: Any?): Boolean = false
    override fun toString(): String = name

    override fun encode(buf: OtpOutputStream?) {
        TODO("not supported")
    }
}
