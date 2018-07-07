package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.*
import org.elixir_lang.debugger.node.Command
import java.nio.charset.Charset

class Evaluate(
        private val pid: OtpErlangPid,
        private val module: OtpErlangAtom,
        private val expression: String,
        private val stackPointer: Int
) : Command {
    override fun toMessage(): OtpErlangTuple =
            OtpErlangTuple(arrayOf(
                    OtpErlangAtom("evaluate"),
                    pid,
                    module,
                    OtpErlangBitstr(expression.toByteArray(Charset.forName("UTF-8"))),
                    OtpErlangLong(stackPointer.toLong())
            ))
}
