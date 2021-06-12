// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirKeywordPair extends QuotableKeywordPair {

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @NotNull
  ElixirKeywordKey getKeywordKey();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

  //WARNING: getKeywordKey(...) is skipped
  //matching getKeywordKey(ElixirKeywordPair, ...)
  //methods are not found in ElixirPsiImplUtil

  @NotNull Quotable getKeywordValue();

  @NotNull OtpErlangObject quote();

}
