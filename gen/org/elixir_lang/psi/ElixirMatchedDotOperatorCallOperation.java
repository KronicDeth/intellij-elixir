// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirMatchedDotOperatorCallOperation extends ElixirMatchedExpression, Quotable {

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirOperatorCallArguments getOperatorCallArguments();

  @NotNull
  ElixirOperatorIdentifier getOperatorIdentifier();

  @NotNull
  OtpErlangObject quote();

}
