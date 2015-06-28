// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirNoParenthesesOneArgument extends QuotableArguments {

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
