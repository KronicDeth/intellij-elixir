// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirCaptureExpressionOperation extends PrefixOperation {

  @Nullable
  ElixirAtBlockOperation getAtBlockOperation();

  @Nullable
  ElixirBlockExpression getBlockExpression();

  @Nullable
  ElixirCaptureBlockOperation getCaptureBlockOperation();

  @NotNull
  ElixirCapturePrefixOperator getCapturePrefixOperator();

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

  @NotNull
  OtpErlangObject quote();

}
