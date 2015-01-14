// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirCallArgumentsNoParenthesesMany extends PsiElement {

  @NotNull
  List<ElixirAtCharTokenOrNumberOperation> getAtCharTokenOrNumberOperationList();

  @NotNull
  List<ElixirAtom> getAtomList();

  @Nullable
  ElixirCallArgumentsNoParenthesesKeywords getCallArgumentsNoParenthesesKeywords();

  @NotNull
  List<ElixirCaptureCharTokenOrNumberOperation> getCaptureCharTokenOrNumberOperationList();

  @NotNull
  List<ElixirCharList> getCharListList();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @NotNull
  List<ElixirList> getListList();

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
  List<ElixirSigil> getSigilList();

  @NotNull
  List<ElixirString> getStringList();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @NotNull
  List<ElixirUnaryCharTokenOrNumberOperation> getUnaryCharTokenOrNumberOperationList();

}
