// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirInterpolatedStringHeredocLine extends HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  Body getBody();

  @NotNull
  OtpErlangObject quote(@NotNull Heredoc heredoc, int prefixLength);

}
