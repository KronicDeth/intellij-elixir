// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirMatchedHatOperation extends PsiElement {

  @NotNull
  List<ElixirAccessExpression> getAccessExpressionList();

  @NotNull
  List<ElixirEmptyParentheses> getEmptyParenthesesList();

  @NotNull
  ElixirHatInfixOperator getHatInfixOperator();

  @NotNull
  List<ElixirMatchedAtOperation> getMatchedAtOperationList();

  @Nullable
  ElixirMatchedHatOperation getMatchedHatOperation();

  @NotNull
  List<ElixirMatchedNonNumericCaptureOperation> getMatchedNonNumericCaptureOperationList();

  @NotNull
  List<ElixirMatchedUnaryOperation> getMatchedUnaryOperationList();

  @NotNull
  List<ElixirNoParenthesesCall> getNoParenthesesCallList();

  @NotNull
  List<ElixirNoParenthesesOneExpression> getNoParenthesesOneExpressionList();

}
