package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangInt
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.debugger.node.Command
import java.nio.charset.Charset

/**
 * [file] is passed to Elixir debugger node, so that it can pass it back, so that [module]<->[file] correspondence only
 * needs to be calculated once.
 */
class SetBreakpoint(private val module: String, line: Int, private val file: String) : Command {
    private val line: Int = line + 1

    override fun toMessage(): OtpErlangTuple =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("set_breakpoint"),
                    OtpErlangAtom(module),
                    OtpErlangInt(line),
                    OtpErlangBinary(file.toByteArray(Charset.forName("UTF-8")))
            ))
}
