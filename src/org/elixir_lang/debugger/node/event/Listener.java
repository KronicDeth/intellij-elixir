package org.elixir_lang.debugger.node.event;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.elixir_lang.debugger.node.ProcessSnapshot;

import java.util.List;

public interface Listener {
  void debuggerStarted();
  void failedToInterpretModules(String nodeName, List<String> modules);
  void failedToDebugRemoteNode(String nodeName, String error);
  void unknownMessage(String messageText);
  void failedToSetBreakpoint(String module, String file, int line, String errorMessage);
  void breakpointIsSet(String module, String file, int line);
  void breakpointReached(OtpErlangPid pid, List<ProcessSnapshot> snapshots);
  void debuggerStopped();
}
