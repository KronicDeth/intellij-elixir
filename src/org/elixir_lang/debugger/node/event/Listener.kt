package org.elixir_lang.debugger.node.event

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangPid
import org.elixir_lang.debugger.InterpretedModule
import org.elixir_lang.debugger.node.ProcessSnapshot

interface Listener {
    fun breakpointIsSet(module: String, file: String, line: Int)
    fun breakpointReached(pid: OtpErlangPid, snapshots: List<ProcessSnapshot>)
    fun debuggerStarted()
    fun debuggerStopped()
    fun failedToDebugRemoteNode(nodeName: String, error: OtpErlangObject)
    fun failedToInterpretModules(nodeName: String, errorReasonByModule: Map<String, OtpErlangObject>)
    fun failedToSetBreakpoint(module: String, file: String, line: Int, errorMessage: OtpErlangObject)
    fun interpretedModules(interpretedModuleList: List<InterpretedModule>)
    fun unknownMessage(messageText: String)
}
