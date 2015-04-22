// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirNoParenthesesKeywords extends QuotableKeywordList {

  @NotNull
  List<ElixirNoParenthesesKeywordPair> getNoParenthesesKeywordPairList();

  List<QuotableKeywordPair> quotableKeywordPairList();

  @NotNull
  OtpErlangObject quote();

}
