// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirNoParenthesesManyArguments extends Arguments, QuotableArguments {

  @Nullable
  ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments getNoParenthesesManyPositionalAndMaybeKeywordsArguments();

  @Nullable
  ElixirNoParenthesesOnePositionalAndKeywordsArguments getNoParenthesesOnePositionalAndKeywordsArguments();

  @NotNull
  PsiElement[] arguments();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
