// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirHatOperation extends PsiElement {

  @Nullable
  ElixirAtom getAtom();

  @Nullable
  ElixirCharList getCharList();

  @Nullable
  ElixirCharListHeredoc getCharListHeredoc();

  @Nullable
  ElixirHatOperation getHatOperation();

  @Nullable
  ElixirSigil getSigil();

  @Nullable
  ElixirString getString();

  @Nullable
  ElixirStringHeredoc getStringHeredoc();

  @Nullable
  ElixirUnaryOperation getUnaryOperation();

}
