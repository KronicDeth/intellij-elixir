package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.node.Command

class Connect(private val name: String) : Command {
    override fun toMessage() = OtpErlangTuple(arrayOf(
            OtpErlangAtom("connect"),
            OtpErlangAtom(name)
    ))
}
