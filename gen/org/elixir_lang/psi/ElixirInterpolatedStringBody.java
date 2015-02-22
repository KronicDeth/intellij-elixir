// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirInterpolatedStringBody extends InterpolatedBody {

  @NotNull
  List<ElixirEscapedCharacter> getEscapedCharacterList();

  @NotNull
  List<ElixirHexadecimalEscapeSequence> getHexadecimalEscapeSequenceList();

  @NotNull
  List<ElixirInterpolation> getInterpolationList();

}
