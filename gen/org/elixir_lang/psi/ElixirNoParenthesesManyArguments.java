// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirNoParenthesesManyArguments extends PsiElement {

  @NotNull
  List<ElixirAlias> getAliasList();

  @NotNull
  List<ElixirAtCharTokenOrNumberOperation> getAtCharTokenOrNumberOperationList();

  @NotNull
  List<ElixirAtom> getAtomList();

  @NotNull
  List<ElixirCaptureCharTokenOrNumberOperation> getCaptureCharTokenOrNumberOperationList();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  List<ElixirCharListLine> getCharListLineList();

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @NotNull
  List<ElixirInterpolatedCharListSigilHeredoc> getInterpolatedCharListSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedCharListSigilLine> getInterpolatedCharListSigilLineList();

  @NotNull
  List<ElixirInterpolatedRegexHeredoc> getInterpolatedRegexHeredocList();

  @NotNull
  List<ElixirInterpolatedRegexLine> getInterpolatedRegexLineList();

  @NotNull
  List<ElixirInterpolatedSigilHeredoc> getInterpolatedSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedSigilLine> getInterpolatedSigilLineList();

  @NotNull
  List<ElixirInterpolatedStringSigilHeredoc> getInterpolatedStringSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedStringSigilLine> getInterpolatedStringSigilLineList();

  @NotNull
  List<ElixirInterpolatedWordsHeredoc> getInterpolatedWordsHeredocList();

  @NotNull
  List<ElixirList> getListList();

  @NotNull
  List<ElixirLiteralCharListBody> getLiteralCharListBodyList();

  @NotNull
  List<ElixirLiteralCharListSigilHeredoc> getLiteralCharListSigilHeredocList();

  @NotNull
  List<ElixirLiteralRegexBody> getLiteralRegexBodyList();

  @NotNull
  List<ElixirLiteralRegexHeredoc> getLiteralRegexHeredocList();

  @NotNull
  List<ElixirLiteralSigilBody> getLiteralSigilBodyList();

  @NotNull
  List<ElixirLiteralSigilHeredoc> getLiteralSigilHeredocList();

  @NotNull
  List<ElixirLiteralStringBody> getLiteralStringBodyList();

  @NotNull
  List<ElixirLiteralStringSigilHeredoc> getLiteralStringSigilHeredocList();

  @NotNull
  List<ElixirLiteralWordsHeredoc> getLiteralWordsHeredocList();

  @NotNull
  List<ElixirMatchedAtOperation> getMatchedAtOperationList();

  @NotNull
  List<ElixirMatchedHatOperation> getMatchedHatOperationList();

  @NotNull
  List<ElixirMatchedMultiplicationOperation> getMatchedMultiplicationOperationList();

  @NotNull
  List<ElixirMatchedNonNumericCaptureOperation> getMatchedNonNumericCaptureOperationList();

  @NotNull
  List<ElixirMatchedUnaryOperation> getMatchedUnaryOperationList();

  @Nullable
  ElixirNoParenthesesKeywords getNoParenthesesKeywords();

  @NotNull
  List<ElixirNoParenthesesManyStrictNoParenthesesExpression> getNoParenthesesManyStrictNoParenthesesExpressionList();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsQualifiedCall> getNoParenthesesNoArgumentsQualifiedCallList();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable> getNoParenthesesNoArgumentsUnqualifiedCallOrVariableList();

  @NotNull
  List<ElixirNumber> getNumberList();

  @NotNull
  List<ElixirQualifiedAlias> getQualifiedAliasList();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @NotNull
  List<ElixirStringLine> getStringLineList();

  @NotNull
  List<ElixirUnaryCharTokenOrNumberOperation> getUnaryCharTokenOrNumberOperationList();

}
