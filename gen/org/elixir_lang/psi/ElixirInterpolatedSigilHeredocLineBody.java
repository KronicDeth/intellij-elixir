// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirInterpolatedSigilHeredocLineBody extends Body {

  @NotNull
  List<ElixirEscapedCharacter> getEscapedCharacterList();

  @NotNull
  List<ElixirHexadecimalEscapePrefix> getHexadecimalEscapePrefixList();

  @NotNull
  List<ElixirInterpolation> getInterpolationList();

  @NotNull
  List<ElixirSigilHexadecimalEscapeSequence> getSigilHexadecimalEscapeSequenceList();

}
