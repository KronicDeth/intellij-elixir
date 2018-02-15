package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProcessSnapshot {
  private final OtpErlangPid myPid;
  private final List<TraceElement> myStack;

  public ProcessSnapshot(@NotNull OtpErlangPid pid, @NotNull List<TraceElement> stack) {
    myPid = pid;
    myStack = stack;
  }

  @NotNull
  public OtpErlangPid getPid() {
    return myPid;
  }

  @NotNull
  public String getPidString() {
    return myPid.toString();
  }

  @NotNull
  public List<TraceElement> getStack() {
    return myStack;
  }
}
