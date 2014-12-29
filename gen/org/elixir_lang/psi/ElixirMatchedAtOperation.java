// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirMatchedAtOperation extends PsiElement {

  @Nullable
  ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation();

  @NotNull
  ElixirAtPrefixOperator getAtPrefixOperator();

  @Nullable
  ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation();

  @Nullable
  ElixirCharList getCharList();

  @Nullable
  ElixirCharListHeredoc getCharListHeredoc();

  @Nullable
  ElixirEmptyParentheses getEmptyParentheses();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation();

  @Nullable
  ElixirMatchedUnaryOperation getMatchedUnaryOperation();

  @Nullable
  ElixirMaxExpression getMaxExpression();

  @Nullable
  ElixirNoParenthesesCall getNoParenthesesCall();

  @Nullable
  ElixirNoParenthesesOneExpression getNoParenthesesOneExpression();

  @Nullable
  ElixirNumber getNumber();

  @Nullable
  ElixirSigil getSigil();

  @Nullable
  ElixirString getString();

  @Nullable
  ElixirStringHeredoc getStringHeredoc();

  @Nullable
  ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation();

}
