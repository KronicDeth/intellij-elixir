// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirNoParenthesesOnePositionalAndKeywordsArguments extends QuotableArguments {

  @NotNull
  ElixirNoParenthesesFirstPositional getNoParenthesesFirstPositional();

  @NotNull
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
