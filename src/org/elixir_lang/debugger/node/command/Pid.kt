package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangPid
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.node.Command

class Pid(private val name: String, private val pid: OtpErlangPid) : Command {
    override fun toMessage(): OtpErlangTuple = OtpErlangTuple(arrayOf(OtpErlangAtom(name), pid))
}
