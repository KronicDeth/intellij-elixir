package org.elixir_lang.debugger.node.event

import com.ericsson.otp.erlang.OtpErlangPid
import org.elixir_lang.debugger.node.ProcessSnapshot

interface Listener {
    fun breakpointIsSet(module: String, file: String, line: Int)
    fun breakpointReached(pid: OtpErlangPid, snapshots: List<ProcessSnapshot>)
    fun debuggerStarted()
    fun debuggerStopped()
    fun failedToDebugRemoteNode(nodeName: String, error: String)
    fun failedToInterpretModules(nodeName: String, modules: List<String>)
    fun failedToSetBreakpoint(module: String, file: String, line: Int, errorMessage: String)
    fun unknownMessage(messageText: String)
}
