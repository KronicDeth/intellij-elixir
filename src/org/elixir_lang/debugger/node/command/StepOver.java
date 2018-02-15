package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;

public class StepOver extends Pid {
  public StepOver(@NotNull OtpErlangPid pid) {
    super("step_over", pid);
  }
}
