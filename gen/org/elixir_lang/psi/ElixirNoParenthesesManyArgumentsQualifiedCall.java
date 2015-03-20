// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirNoParenthesesManyArgumentsQualifiedCall extends PsiElement {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtNumericOperation getAtNumericOperation();

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirAtomKeyword getAtomKeyword();

  @Nullable
  ElixirCaptureNumericOperation getCaptureNumericOperation();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  List<ElixirCharListLine> getCharListLineList();

  @Nullable
  ElixirCharToken getCharToken();

  @Nullable
  ElixirEmptyBlock getEmptyBlock();

  @NotNull
  ElixirInfixDotOperator getInfixDotOperator();

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
  ElixirInterpolatedWordsLine getInterpolatedWordsLine();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirLiteralCharListSigilHeredoc getLiteralCharListSigilHeredoc();

  @Nullable
  ElixirLiteralCharListSigilLine getLiteralCharListSigilLine();

  @Nullable
  ElixirLiteralRegexHeredoc getLiteralRegexHeredoc();

  @Nullable
  ElixirLiteralRegexLine getLiteralRegexLine();

  @Nullable
  ElixirLiteralSigilHeredoc getLiteralSigilHeredoc();

  @Nullable
  ElixirLiteralSigilLine getLiteralSigilLine();

  @Nullable
  ElixirLiteralStringSigilHeredoc getLiteralStringSigilHeredoc();

  @Nullable
  ElixirLiteralStringSigilLine getLiteralStringSigilLine();

  @Nullable
  ElixirLiteralWordsHeredoc getLiteralWordsHeredoc();

  @Nullable
  ElixirLiteralWordsLine getLiteralWordsLine();

  @Nullable
  ElixirMatchedNonNumericAtOperation getMatchedNonNumericAtOperation();

  @Nullable
  ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation();

  @Nullable
  ElixirMatchedNonNumericUnaryOperation getMatchedNonNumericUnaryOperation();

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @Nullable
  ElixirNoParenthesesNoArgumentsQualifiedCall getNoParenthesesNoArgumentsQualifiedCall();

  @Nullable
  ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable getNoParenthesesNoArgumentsUnqualifiedCallOrVariable();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @Nullable
  ElixirNumber getNumber();

  @Nullable
  ElixirQualifiedAlias getQualifiedAlias();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @NotNull
  List<ElixirStringLine> getStringLineList();

  @Nullable
  ElixirUnaryNumericOperation getUnaryNumericOperation();

}
