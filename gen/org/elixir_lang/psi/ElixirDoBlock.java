// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirDoBlock extends PsiElement {

  @Nullable
  ElixirBlockList getBlockList();

  @NotNull
  ElixirDoEOL getDoEOL();

  @Nullable
  ElixirEndEOL getEndEOL();

  @Nullable
  ElixirStab getStab();

  @Nullable
  ElixirStabEOL getStabEOL();

}
