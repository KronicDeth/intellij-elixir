package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangInt;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import org.elixir_lang.debugger.node.Command;
import org.jetbrains.annotations.NotNull;

public class RemoveBreakpoint implements Command {
  private final String myModule;
  private final int myLine;

  public RemoveBreakpoint(@NotNull String module, int line) {
    myModule = module;
    myLine = line + 1;
  }

  @NotNull
  @Override
  public OtpErlangTuple toMessage() {
    return new OtpErlangTuple(new OtpErlangObject[] {
      new OtpErlangAtom("remove_breakpoint"),
      new OtpErlangAtom(myModule),
      new OtpErlangInt(myLine)
    });
  }
}
