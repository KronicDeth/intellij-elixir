// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirBracketArguments extends Quotable {

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @Nullable
  ElixirKeywords getKeywords();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  @NotNull
  OtpErlangObject quote();

}
