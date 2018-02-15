package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.node.Command

class RunTask : Command {
    override fun toMessage(): OtpErlangTuple = OtpErlangTuple(OtpErlangAtom("run_task"))
}
