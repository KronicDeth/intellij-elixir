package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;

public class StepOut extends Pid {
  public StepOut(@NotNull OtpErlangPid pid) {
    super("step_out", pid);
  }
}
