package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import org.elixir_lang.debugger.node.Command

class ReasonByUninterpretable : Command {
    override fun toMessage() = OtpErlangAtom("reason_by_uninterpretable")
}
