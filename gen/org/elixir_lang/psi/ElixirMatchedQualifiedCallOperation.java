// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedQualifiedCallOperation extends ElixirMatchedExpression, Quotable {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @Nullable
  ElixirMatchedCallArguments getMatchedCallArguments();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  OtpErlangObject quote();

}
