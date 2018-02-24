package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import org.elixir_lang.debugger.node.Command

class RunTask : Command {
    override fun toMessage() = OtpErlangAtom("run_task")
}
