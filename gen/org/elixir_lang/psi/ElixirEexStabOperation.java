// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirEexStabOperation extends PsiElement {

  @Nullable
  ElixirEexStabBody getEexStabBody();

  @NotNull
  ElixirStabInfixOperator getStabInfixOperator();

  @Nullable
  ElixirStabNoParenthesesSignature getStabNoParenthesesSignature();

  @Nullable
  ElixirStabParenthesesSignature getStabParenthesesSignature();

}
