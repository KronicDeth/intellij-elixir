// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirList extends KeywordList {

  @NotNull
  List<ElixirListKeywordPair> getListKeywordPairList();

  List<KeywordPair> getKeywordPairList();

  @NotNull
  OtpErlangObject quote();

}
