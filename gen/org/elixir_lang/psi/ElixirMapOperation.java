// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMapOperation extends Quotable {

  @NotNull
  ElixirMapArguments getMapArguments();

  @NotNull
  ElixirMapPrefixOperator getMapPrefixOperator();

  @NotNull
  OtpErlangObject quote();

}
