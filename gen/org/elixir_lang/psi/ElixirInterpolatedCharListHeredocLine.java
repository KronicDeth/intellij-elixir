// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirInterpolatedCharListHeredocLine extends HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  Body getBody();

  @NotNull
  OtpErlangObject quote(@NotNull Heredoc heredoc, int prefixLength);

}
