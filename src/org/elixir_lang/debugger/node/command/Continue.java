package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangPid;
import org.jetbrains.annotations.NotNull;

public class Continue extends Pid {
  public Continue(@NotNull OtpErlangPid pid) {
    super("continue", pid);
  }
}
