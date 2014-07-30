// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirAssocUpdate extends PsiElement {

  @Nullable
  ElixirAssocOperatorEOL getAssocOperatorEOL();

  @NotNull
  List<ElixirExpression> getExpressionList();

  @Nullable
  ElixirMapExpression getMapExpression();

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @NotNull
  ElixirPipeOperatorEOL getPipeOperatorEOL();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

}
