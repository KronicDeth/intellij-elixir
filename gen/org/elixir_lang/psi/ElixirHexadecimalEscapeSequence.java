// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import org.jetbrains.annotations.Nullable;

public interface ElixirHexadecimalEscapeSequence extends EscapeSequence {

  @Nullable
  ElixirEnclosedHexadecimalEscapeSequence getEnclosedHexadecimalEscapeSequence();

  @Nullable
  ElixirOpenHexadecimalEscapeSequence getOpenHexadecimalEscapeSequence();

  int codePoint();

}
