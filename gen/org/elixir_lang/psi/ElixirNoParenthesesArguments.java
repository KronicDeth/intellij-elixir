// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirNoParenthesesArguments extends QuotableArguments {

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  List<ElixirNoParenthesesExpression> getNoParenthesesExpressionList();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @Nullable
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
