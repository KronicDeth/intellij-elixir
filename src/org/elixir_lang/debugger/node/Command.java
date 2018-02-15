package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangTuple;
import org.jetbrains.annotations.NotNull;

public interface Command {
  @NotNull
  OtpErlangTuple toMessage();
}
