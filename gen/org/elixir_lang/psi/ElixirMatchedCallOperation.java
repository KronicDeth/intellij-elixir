// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMatchedCallOperation extends ElixirMatchedExpression, QuotableCall {

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject[] quoteArguments();

  @NotNull
  OtpErlangObject quoteIdentifier();

}
