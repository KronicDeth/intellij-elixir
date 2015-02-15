// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirInterpolatedCharListSigilHeredoc extends PsiElement {

  @NotNull
  ElixirHeredocPrefix getHeredocPrefix();

  @NotNull
  List<ElixirInterpolatedCharListHeredocLine> getInterpolatedCharListHeredocLineList();

}
