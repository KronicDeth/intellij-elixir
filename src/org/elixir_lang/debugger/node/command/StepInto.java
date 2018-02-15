package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;

public class StepInto extends Pid {
  public StepInto(@NotNull OtpErlangPid pid) {
    super("step_into", pid);
  }
}
