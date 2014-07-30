// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ElixirMatchedExpression extends PsiElement {

  @Nullable
  ElixirAccessExpression getAccessExpression();

  @Nullable
  ElixirAtOperatorEOL getAtOperatorEOL();

  @Nullable
  ElixirCaptureOperatorEOL getCaptureOperatorEOL();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirMatchedOperatorExpression getMatchedOperatorExpression();

  @Nullable
  ElixirNoParenthesesExpression getNoParenthesesExpression();

  @Nullable
  ElixirNoParenthesesOneExpression getNoParenthesesOneExpression();

  @Nullable
  ElixirUnaryOperatorEOL getUnaryOperatorEOL();

}
