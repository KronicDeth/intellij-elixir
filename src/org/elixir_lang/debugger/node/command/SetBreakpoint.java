package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.*;
import org.elixir_lang.debugger.node.Command;
import org.jetbrains.annotations.NotNull;

public class SetBreakpoint implements Command {
  private final String myModule;
  private final int myLine;
  private final String myFile;

  public SetBreakpoint(@NotNull String module, int line, @NotNull String file) {
    myModule = module;
    myLine = line + 1;
    myFile = file;
  }

  @NotNull
  @Override
  public OtpErlangTuple toMessage() {
    return new OtpErlangTuple(new OtpErlangObject[]{
      new OtpErlangAtom("set_breakpoint"),
      new OtpErlangAtom(myModule),
      new OtpErlangInt(myLine),
      new OtpErlangString(myFile)
    });
  }
}
