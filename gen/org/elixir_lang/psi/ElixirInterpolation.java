// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirInterpolation extends PsiElement {

  @NotNull
  List<ElixirAdjacentExpression> getAdjacentExpressionList();

  @NotNull
  List<ElixirCaptureMatchedExpressionOperation> getCaptureMatchedExpressionOperationList();

  @NotNull
  List<ElixirCaptureTailExpressionOperation> getCaptureTailExpressionOperationList();

  @NotNull
  List<ElixirEndOfExpression> getEndOfExpressionList();

}
