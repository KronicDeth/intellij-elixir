// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirAdjacentExpression extends Quotable {

  @Nullable
  ElixirAtBlockOperation getAtBlockOperation();

  @Nullable
  ElixirBlockExpression getBlockExpression();

  @Nullable
  ElixirCaptureBlockOperation getCaptureBlockOperation();

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirUnaryBlockOperation getUnaryBlockOperation();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

  @NotNull
  OtpErlangObject quote();

}
