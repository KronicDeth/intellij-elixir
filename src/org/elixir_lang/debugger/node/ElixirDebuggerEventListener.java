package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangPid;

import java.util.List;

public interface ElixirDebuggerEventListener {
  void debuggerStarted();
  void failedToInterpretModules(String nodeName, List<String> modules);
  void failedToDebugRemoteNode(String nodeName, String error);
  void unknownMessage(String messageText);
  void failedToSetBreakpoint(String module, String file, int line, String errorMessage);
  void breakpointIsSet(String module, String file, int line);
  void breakpointReached(OtpErlangPid pid, List<ElixirProcessSnapshot> snapshots);
  void debuggerStopped();
}
