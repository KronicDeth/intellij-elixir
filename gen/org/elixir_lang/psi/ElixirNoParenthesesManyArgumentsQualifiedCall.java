// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirNoParenthesesManyArgumentsQualifiedCall extends PsiElement {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation();

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation();

  @NotNull
  List<ElixirCharList> getCharListList();

  @NotNull
  List<ElixirCharListHeredoc> getCharListHeredocList();

  @NotNull
  ElixirInfixDotOperator getInfixDotOperator();

  @Nullable
  ElixirInterpolatedCharListSigilHeredoc getInterpolatedCharListSigilHeredoc();

  @Nullable
  ElixirInterpolatedRegexHeredoc getInterpolatedRegexHeredoc();

  @Nullable
  ElixirInterpolatedSigilHeredoc getInterpolatedSigilHeredoc();

  @Nullable
  ElixirInterpolatedStringSigilHeredoc getInterpolatedStringSigilHeredoc();

  @Nullable
  ElixirInterpolatedWordsHeredoc getInterpolatedWordsHeredoc();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirMatchedAtOperation getMatchedAtOperation();

  @Nullable
  ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation();

  @Nullable
  ElixirMatchedUnaryOperation getMatchedUnaryOperation();

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
  List<ElixirString> getStringList();

  @NotNull
  List<ElixirStringHeredoc> getStringHeredocList();

  @Nullable
  ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation();

}
