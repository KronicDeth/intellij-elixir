// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirHexadecimalEscapeSequence extends EscapeSequence {

  @Nullable
  ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence();

  @Nullable
  ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence();

  int codePoint();

}
