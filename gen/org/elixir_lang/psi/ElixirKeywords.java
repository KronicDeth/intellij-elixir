// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirKeywords extends QuotableKeywordList {

  @NotNull
  List<ElixirKeywordPair> getKeywordPairList();

  List<QuotableKeywordPair> quotableKeywordPairList();

  @RequiresReadLock
  @NotNull OtpErlangObject quote();

}
