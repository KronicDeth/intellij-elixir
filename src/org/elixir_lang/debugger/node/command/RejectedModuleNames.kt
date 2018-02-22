package org.elixir_lang.debugger.node.command

import com.ericsson.otp.erlang.OtpErlangAtom
import org.elixir_lang.debugger.node.Command

class RejectedModuleNames : Command {
    override fun toMessage() = OtpErlangAtom("rejected_module_names")
}
