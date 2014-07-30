// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirBracketExpression extends PsiElement {

  @Nullable
  ElixirAccessExpression getAccessExpression();

  @NotNull
  ElixirBracketArgument getBracketArgument();

  @Nullable
  ElixirDotBracketIdentifier getDotBracketIdentifier();

}
