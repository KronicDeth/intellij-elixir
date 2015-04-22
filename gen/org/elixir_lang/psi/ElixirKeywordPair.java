// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirKeywordPair extends QuotableKeywordPair {

  @NotNull
  ElixirEmptyParentheses getEmptyParentheses();

  @NotNull
  ElixirKeywordKey getKeywordKey();

  Quotable getKeywordValue();

  @NotNull
  OtpErlangObject quote();

}
