// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedDotCallOperation extends ElixirMatchedExpression, QuotableCall {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirParenthesesArguments getParenthesesArguments();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject[] quoteArguments();

  @NotNull
  OtpErlangObject quoteIdentifier();

}
