// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirMatchedNonNumericCaptureOperation extends PsiElement {

  @Nullable
  ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation();

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation();

  @NotNull
  ElixirCapturePrefixOperator getCapturePrefixOperator();

  @Nullable
  ElixirCharList getCharList();

  @Nullable
  ElixirCharListHeredoc getCharListHeredoc();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirMatchedAtOperation getMatchedAtOperation();

  @Nullable
  ElixirMatchedHatOperation getMatchedHatOperation();

  @Nullable
  ElixirMatchedMultiplicationOperation getMatchedMultiplicationOperation();

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
  ElixirSigil getSigil();

  @Nullable
  ElixirString getString();

  @Nullable
  ElixirStringHeredoc getStringHeredoc();

  @Nullable
  ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation();

}
