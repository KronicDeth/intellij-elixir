package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangTuple;
import org.elixir_lang.debugger.node.Command;
import org.jetbrains.annotations.NotNull;

public class RunTask implements Command {
  @NotNull
  @Override
  public OtpErlangTuple toMessage() {
    return new OtpErlangTuple(new OtpErlangAtom("run_task"));
  }
}
