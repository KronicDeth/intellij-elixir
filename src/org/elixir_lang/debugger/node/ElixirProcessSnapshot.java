package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirProcessSnapshot {
  private final OtpErlangPid myPid;
  private final String myStatus;
  private final String myBreakModule;
  private final String myBreakFile;
  private final int myBreakLine;
  private final String myExitReason;
  private final List<ElixirTraceElement> myStack;

  public ElixirProcessSnapshot(@NotNull OtpErlangPid pid, /*@NotNull ElixirTraceElement init,*/ @NotNull String status,
                               @Nullable String breakModule, int breakLine, String breakFile,
                               @Nullable String exitReason, @NotNull List<ElixirTraceElement> stack) {
    myPid = pid;
    myStatus = status;
    myBreakModule = breakModule;
    myBreakLine = breakLine;
    myBreakFile = breakFile;
    myExitReason = exitReason;
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
  public String getStatus() {
    return myStatus;
  }

  @Nullable
  public String getBreakModule() {
    return myBreakModule;
  }

  public int getBreakLine() {
    return myBreakLine;
  }

  @Nullable
  public String getExitReason() {
    return myExitReason;
  }

  @NotNull
  public List<ElixirTraceElement> getStack() {
    return myStack;
  }

  public String getBreakFile() {
    return myBreakFile;
  }
}
