// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ElixirSigil extends PsiElement {

  @Nullable
  ElixirInterpolatedCharListBody getInterpolatedCharListBody();

  @Nullable
  ElixirInterpolatedStringBody getInterpolatedStringBody();

  @NotNull
  List<ElixirInterpolation> getInterpolationList();

}
