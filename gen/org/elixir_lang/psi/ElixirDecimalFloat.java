// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirDecimalFloat extends ElixirMatchedExpression, Quotable {

  @Nullable
  ElixirDecimalFloatExponent getDecimalFloatExponent();

  @NotNull
  ElixirDecimalFloatFractional getDecimalFloatFractional();

  @NotNull
  ElixirDecimalFloatIntegral getDecimalFloatIntegral();

  @NotNull
  OtpErlangObject quote();

}
