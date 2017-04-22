package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirProcessSnapshot {
  private final OtpErlangPid myPid;
  private final List<ElixirTraceElement> myStack;

  public ElixirProcessSnapshot(@NotNull OtpErlangPid pid, @NotNull List<ElixirTraceElement> stack) {
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
  public List<ElixirTraceElement> getStack() {
    return myStack;
  }
}
