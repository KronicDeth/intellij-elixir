// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirUnmatchedDotCall extends ElixirUnmatchedExpression, Quotable {

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  List<ElixirParenthesesArguments> getParenthesesArgumentsList();

  @NotNull
  ElixirUnmatchedExpression getUnmatchedExpression();

  @NotNull
  OtpErlangObject quote();

}
