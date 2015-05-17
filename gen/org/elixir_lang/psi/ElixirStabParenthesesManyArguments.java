// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirStabParenthesesManyArguments extends QuotableArguments {

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
