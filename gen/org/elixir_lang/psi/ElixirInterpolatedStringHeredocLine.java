// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirInterpolatedStringHeredocLine extends HeredocLine {

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  InterpolatedBody getInterpolatedBody();

  @NotNull
  OtpErlangObject quote(Heredoc heredoc, int prefixLength);

}
