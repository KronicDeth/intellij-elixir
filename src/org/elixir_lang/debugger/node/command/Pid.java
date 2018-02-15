package org.elixir_lang.debugger.node.command;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangTuple;
import org.elixir_lang.debugger.node.Command;
import org.jetbrains.annotations.NotNull;

public class Pid implements Command {
  private final String myName;
  private final OtpErlangPid myPid;

  public Pid(@NotNull String cmdName, @NotNull OtpErlangPid pid) {
    myName = cmdName;
    myPid = pid;
  }

  @NotNull
  @Override
  public OtpErlangTuple toMessage() {
    return new OtpErlangTuple(new OtpErlangObject[]{new OtpErlangAtom(myName), myPid});
  }
}
