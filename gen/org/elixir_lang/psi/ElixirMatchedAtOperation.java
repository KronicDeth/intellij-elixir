// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedAtOperation extends PsiElement {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation();

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation();

  @Nullable
  ElixirCharListHeredoc getCharListHeredoc();

  @Nullable
  ElixirCharListLine getCharListLine();

  @Nullable
  ElixirInterpolatedCharListSigilHeredoc getInterpolatedCharListSigilHeredoc();

  @Nullable
  ElixirInterpolatedCharListSigilLine getInterpolatedCharListSigilLine();

  @Nullable
  ElixirInterpolatedRegexHeredoc getInterpolatedRegexHeredoc();

  @Nullable
  ElixirInterpolatedRegexLine getInterpolatedRegexLine();

  @Nullable
  ElixirInterpolatedSigilHeredoc getInterpolatedSigilHeredoc();

  @Nullable
  ElixirInterpolatedSigilLine getInterpolatedSigilLine();

  @Nullable
  ElixirInterpolatedStringSigilHeredoc getInterpolatedStringSigilHeredoc();

  @Nullable
  ElixirInterpolatedStringSigilLine getInterpolatedStringSigilLine();

  @Nullable
  ElixirInterpolatedWordsHeredoc getInterpolatedWordsHeredoc();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirLiteralCharListBody getLiteralCharListBody();

  @Nullable
  ElixirLiteralCharListSigilHeredoc getLiteralCharListSigilHeredoc();

  @Nullable
  ElixirLiteralRegexBody getLiteralRegexBody();

  @Nullable
  ElixirLiteralRegexHeredoc getLiteralRegexHeredoc();

  @Nullable
  ElixirLiteralSigilBody getLiteralSigilBody();

  @Nullable
  ElixirLiteralSigilHeredoc getLiteralSigilHeredoc();

  @Nullable
  ElixirLiteralStringBody getLiteralStringBody();

  @Nullable
  ElixirLiteralStringSigilHeredoc getLiteralStringSigilHeredoc();

  @Nullable
  ElixirLiteralWordsHeredoc getLiteralWordsHeredoc();

  @Nullable
  ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation();

  @Nullable
  ElixirMatchedUnaryOperation getMatchedUnaryOperation();

  @Nullable
  ElixirNoParenthesesManyArgumentsQualifiedCall getNoParenthesesManyArgumentsQualifiedCall();

  @Nullable
  ElixirNoParenthesesManyArgumentsUnqualifiedCall getNoParenthesesManyArgumentsUnqualifiedCall();

  @Nullable
  ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable getNoParenthesesNoArgumentsUnqualifiedCallOrVariable();

  @Nullable
  ElixirNumber getNumber();

  @Nullable
  ElixirStringHeredoc getStringHeredoc();

  @Nullable
  ElixirStringLine getStringLine();

  @Nullable
  ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation();

}
