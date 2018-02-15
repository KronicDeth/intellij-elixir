package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangInt
import com.ericsson.otp.erlang.OtpErlangString
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.node.Command

class SetBreakpoint(private val module: String, line: Int, private val file: String) : Command {
    private val line: Int = line + 1

    override fun toMessage(): OtpErlangTuple =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("set_breakpoint"),
                    OtpErlangAtom(module),
                    OtpErlangInt(line),
                    OtpErlangString(file)
            ))
}
