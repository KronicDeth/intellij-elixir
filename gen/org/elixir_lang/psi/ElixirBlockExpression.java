// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirBlockExpression extends PsiElement {

  @Nullable
  ElixirCallArgumentsNoParenthesesAll getCallArgumentsNoParenthesesAll();

  @NotNull
  List<ElixirCallArgumentsParentheses> getCallArgumentsParenthesesList();

  @NotNull
  ElixirDoBlock getDoBlock();

  @Nullable
  ElixirDotDoIdentifier getDotDoIdentifier();

  @Nullable
  ElixirDotIdentifier getDotIdentifier();

  @Nullable
  ElixirParenthesesCall getParenthesesCall();

}
