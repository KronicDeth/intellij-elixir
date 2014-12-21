// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirMatchedExpressionOrTailExpressionOperation extends ElixirMatchedExpressionMatchTailExpressionOperation {

  @NotNull
  ElixirMatchedExpressionAndTailExpressionOperation getMatchedExpressionAndTailExpressionOperation();

  @Nullable
  ElixirMatchedExpressionOrMatchedExpressionOperation getMatchedExpressionOrMatchedExpressionOperation();

  @Nullable
  ElixirOrInfixOperator getOrInfixOperator();

}
