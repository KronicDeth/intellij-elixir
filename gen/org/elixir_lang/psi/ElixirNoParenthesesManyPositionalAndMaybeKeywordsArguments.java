// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments extends Arguments, QuotableArguments {

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  List<ElixirNoParenthesesExpression> getNoParenthesesExpressionList();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @NotNull
  PsiElement[] arguments();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
