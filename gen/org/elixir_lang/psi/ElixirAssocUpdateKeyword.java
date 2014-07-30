// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirAssocUpdateKeyword extends PsiElement {

  @NotNull
  ElixirKeyword getKeyword();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @NotNull
  ElixirPipeOperatorEOL getPipeOperatorEOL();

  @Nullable
  ElixirUnmatchedExpression getUnmatchedExpression();

}
