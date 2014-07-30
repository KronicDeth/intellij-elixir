// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirBracketAtExpression extends PsiElement {

  @Nullable
  ElixirAccessExpression getAccessExpression();

  @NotNull
  ElixirAtOperatorEOL getAtOperatorEOL();

  @NotNull
  ElixirBracketArgument getBracketArgument();

  @Nullable
  ElixirDotBracketIdentifier getDotBracketIdentifier();

}
