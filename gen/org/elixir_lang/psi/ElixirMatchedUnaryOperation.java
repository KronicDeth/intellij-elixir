// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirMatchedUnaryOperation extends PsiElement {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation();

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation();

  @Nullable
  ElixirCharList getCharList();

  @Nullable
  ElixirCharListHeredoc getCharListHeredoc();

  @Nullable
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  @Nullable
  ElixirInterpolatedCharListSigilHeredoc getInterpolatedCharListSigilHeredoc();

  @Nullable
  ElixirInterpolatedRegexBody getInterpolatedRegexBody();

  @Nullable
  ElixirInterpolatedRegexHeredoc getInterpolatedRegexHeredoc();

  @Nullable
  ElixirInterpolatedSigilBody getInterpolatedSigilBody();

  @Nullable
  ElixirInterpolatedSigilHeredoc getInterpolatedSigilHeredoc();

  @Nullable
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  @Nullable
  ElixirInterpolatedStringSigilHeredoc getInterpolatedStringSigilHeredoc();

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
  ElixirMatchedAtOperation getMatchedAtOperation();

  @Nullable
  ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation();

  @Nullable
  ElixirMatchedUnaryOperation getMatchedUnaryOperation();

  @Nullable
  ElixirNoParenthesesManyArgumentsQualifiedCall getNoParenthesesManyArgumentsQualifiedCall();

  @Nullable
  ElixirNoParenthesesManyArgumentsUnqualifiedCall getNoParenthesesManyArgumentsUnqualifiedCall();

  @Nullable
  ElixirNoParenthesesNoArgumentsQualifiedCall getNoParenthesesNoArgumentsQualifiedCall();

  @Nullable
  ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable getNoParenthesesNoArgumentsUnqualifiedCallOrVariable();

  @Nullable
  ElixirNumber getNumber();

  @Nullable
  ElixirQualifiedAlias getQualifiedAlias();

  @Nullable
  ElixirString getString();

  @Nullable
  ElixirStringHeredoc getStringHeredoc();

  @Nullable
  ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation();

  @NotNull
  ElixirUnaryPrefixOperator getUnaryPrefixOperator();

}
