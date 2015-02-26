// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirMatchedHatOperation extends PsiElement {

  @NotNull
  List<ElixirAlias> getAliasList();

  @NotNull
  List<ElixirAtCharTokenOrNumberOperation> getAtCharTokenOrNumberOperationList();

  @NotNull
  List<ElixirAtom> getAtomList();

  @NotNull
  List<ElixirCaptureCharTokenOrNumberOperation> getCaptureCharTokenOrNumberOperationList();

  @NotNull
  List<ElixirCharList> getCharListList();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  ElixirHatInfixOperator getHatInfixOperator();

  @Nullable
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  @NotNull
  List<ElixirInterpolatedCharListSigilHeredoc> getInterpolatedCharListSigilHeredocList();

  @Nullable
  ElixirInterpolatedRegexBody getInterpolatedRegexBody();

  @NotNull
  List<ElixirInterpolatedRegexHeredoc> getInterpolatedRegexHeredocList();

  @Nullable
  ElixirInterpolatedSigilBody getInterpolatedSigilBody();

  @NotNull
  List<ElixirInterpolatedSigilHeredoc> getInterpolatedSigilHeredocList();

  @Nullable
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  @NotNull
  List<ElixirInterpolatedStringSigilHeredoc> getInterpolatedStringSigilHeredocList();

  @NotNull
  List<ElixirInterpolatedWordsHeredoc> getInterpolatedWordsHeredocList();

  @NotNull
  List<ElixirList> getListList();

  @Nullable
  ElixirLiteralCharListBody getLiteralCharListBody();

  @NotNull
  List<ElixirLiteralCharListSigilHeredoc> getLiteralCharListSigilHeredocList();

  @Nullable
  ElixirLiteralRegexBody getLiteralRegexBody();

  @NotNull
  List<ElixirLiteralRegexHeredoc> getLiteralRegexHeredocList();

  @Nullable
  ElixirLiteralSigilBody getLiteralSigilBody();

  @NotNull
  List<ElixirLiteralSigilHeredoc> getLiteralSigilHeredocList();

  @Nullable
  ElixirLiteralStringBody getLiteralStringBody();

  @NotNull
  List<ElixirLiteralStringSigilHeredoc> getLiteralStringSigilHeredocList();

  @NotNull
  List<ElixirMatchedAtOperation> getMatchedAtOperationList();

  @Nullable
  ElixirMatchedHatOperation getMatchedHatOperation();

  @NotNull
  List<ElixirMatchedNonNumericCaptureOperation> getMatchedNonNumericCaptureOperationList();

  @NotNull
  List<ElixirMatchedUnaryOperation> getMatchedUnaryOperationList();

  @Nullable
  ElixirNoParenthesesManyArgumentsQualifiedCall getNoParenthesesManyArgumentsQualifiedCall();

  @Nullable
  ElixirNoParenthesesManyArgumentsUnqualifiedCall getNoParenthesesManyArgumentsUnqualifiedCall();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsQualifiedCall> getNoParenthesesNoArgumentsQualifiedCallList();

  @NotNull
  List<ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable> getNoParenthesesNoArgumentsUnqualifiedCallOrVariableList();

  @NotNull
  List<ElixirNumber> getNumberList();

  @NotNull
  List<ElixirQualifiedAlias> getQualifiedAliasList();

  @NotNull
  List<ElixirString> getStringList();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @NotNull
  List<ElixirUnaryCharTokenOrNumberOperation> getUnaryCharTokenOrNumberOperationList();

}
