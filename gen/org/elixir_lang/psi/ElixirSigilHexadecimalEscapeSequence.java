// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirSigilHexadecimalEscapeSequence extends EscapeSequence {

  @Nullable
  ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence();

  @NotNull
  ElixirHexadecimalEscapePrefix getHexadecimalEscapePrefix();

  @Nullable
  ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence();

  int codePoint();

}
