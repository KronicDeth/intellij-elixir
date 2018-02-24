package org.elixir_lang.debugger.node;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface Command {
  @NotNull
  OtpErlangObject toMessage();
}
