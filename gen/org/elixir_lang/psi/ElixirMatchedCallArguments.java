// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMatchedCallArguments extends PsiElement {

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @NotNull
  List<ElixirParenthesesArguments> getParenthesesArgumentsList();

  @Nullable
  ElixirUnqualifiedNoParenthesesManyArgumentsCall getUnqualifiedNoParenthesesManyArgumentsCall();

}
