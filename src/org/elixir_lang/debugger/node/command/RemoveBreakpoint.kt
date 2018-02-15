package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangInt
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.node.Command

class RemoveBreakpoint(private val module: String, line: Int) : Command {
    private val line: Int = line + 1

    override fun toMessage(): OtpErlangTuple =
            OtpErlangTuple(arrayOf(OtpErlangAtom("remove_breakpoint"), OtpErlangAtom(module), OtpErlangInt(line)))
}
