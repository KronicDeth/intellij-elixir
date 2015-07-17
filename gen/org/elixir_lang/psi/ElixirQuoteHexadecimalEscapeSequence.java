// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirQuoteHexadecimalEscapeSequence extends EscapeSequence {

  @Nullable
  ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence();

  @NotNull
  ElixirHexadecimalEscapePrefix getHexadecimalEscapePrefix();

  @Nullable
  ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence();

  int codePoint();

}
