// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

public interface ElixirListKeywordPair extends KeywordPair {

  @NotNull
  ElixirKeywordKey getKeywordKey();

  @NotNull
  ElixirKeywordValue getKeywordValue();

  @NotNull
  OtpErlangObject quote();

}
