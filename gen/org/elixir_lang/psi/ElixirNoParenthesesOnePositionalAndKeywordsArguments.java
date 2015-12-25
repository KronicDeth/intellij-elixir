// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface ElixirNoParenthesesOnePositionalAndKeywordsArguments extends Arguments, QuotableArguments {

  @NotNull
  ElixirNoParenthesesFirstPositional getNoParenthesesFirstPositional();

  @NotNull
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @NotNull
  PsiElement[] arguments();

  @NotNull
  OtpErlangObject[] quoteArguments();

}
