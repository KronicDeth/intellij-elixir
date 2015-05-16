// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedQualifiedParenthesesCall extends ElixirMatchedExpression, Quotable {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

  @NotNull
  ElixirRelativeIdentifier getRelativeIdentifier();

  @NotNull
  OtpErlangObject quote();

}
