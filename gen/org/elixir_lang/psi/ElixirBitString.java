// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirBitString extends Quotable {

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @Nullable
  ElixirKeywords getKeywords();

  @NotNull
  List<ElixirUnmatchedExpression> getUnmatchedExpressionList();

  @NotNull OtpErlangObject quote();

}
