// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirSigil extends PsiElement {

  @Nullable
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  @Nullable
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  @NotNull
  List<ElixirInterpolation> getInterpolationList();

}
