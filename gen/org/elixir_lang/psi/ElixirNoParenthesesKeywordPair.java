// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirNoParenthesesKeywordPair extends KeywordPair {

  @NotNull
  ElixirKeywordKey getKeywordKey();

  @NotNull
  ElixirNoParenthesesExpression getNoParenthesesExpression();

  Quotable getKeywordValue();

  @NotNull
  OtpErlangObject quote();

}
