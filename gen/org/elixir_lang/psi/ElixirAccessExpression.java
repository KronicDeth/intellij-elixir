// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirAccessExpression extends PsiElement {

  @Nullable
  ElixirAtOperatorEOL getAtOperatorEOL();

  @Nullable
  ElixirBitString getBitString();

  @Nullable
  ElixirBracketAtExpression getBracketAtExpression();

  @Nullable
  ElixirBracketExpression getBracketExpression();

  @Nullable
  ElixirCaptureOperatorEOL getCaptureOperatorEOL();

  @Nullable
  ElixirCloseParenthesis getCloseParenthesis();

  @Nullable
  ElixirEndEOL getEndEOL();

  @Nullable
  ElixirFnEOL getFnEOL();

  @Nullable
  ElixirList getList();

  @Nullable
  ElixirMap getMap();

  @Nullable
  ElixirMaxExpression getMaxExpression();

  @Nullable
  ElixirOpenParenthesis getOpenParenthesis();

  @Nullable
  ElixirStab getStab();

  @Nullable
  ElixirTuple getTuple();

  @Nullable
  ElixirUnaryOperatorEOL getUnaryOperatorEOL();

}
