// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface ElixirHexadecimalEscapeSequence extends PsiElement {

  @Nullable
  ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence();

  @Nullable
  ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence();

  int codePoint();

}
