// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirStabParenthesesSignature extends Quotable, WhenOperation {

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @NotNull
  ElixirParenthesesArguments getParenthesesArguments();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

  @Nullable
  ElixirWhenInfixOperator getWhenInfixOperator();

  @NotNull
  Quotable leftOperand();

  @NotNull
  Operator operator();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  Quotable rightOperand();

}
