// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;

public class ElixirAccessExpressionImpl extends ASTWrapperPsiElement implements ElixirAccessExpression {

  public ElixirAccessExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAccessExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtOperatorEOL getAtOperatorEOL() {
    return findChildByClass(ElixirAtOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirBitString getBitString() {
    return findChildByClass(ElixirBitString.class);
  }

  @Override
  @Nullable
  public ElixirBracketAtExpression getBracketAtExpression() {
    return findChildByClass(ElixirBracketAtExpression.class);
  }

  @Override
  @Nullable
  public ElixirBracketExpression getBracketExpression() {
    return findChildByClass(ElixirBracketExpression.class);
  }

  @Override
  @Nullable
  public ElixirCaptureOperatorEOL getCaptureOperatorEOL() {
    return findChildByClass(ElixirCaptureOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirCloseParenthesis getCloseParenthesis() {
    return findChildByClass(ElixirCloseParenthesis.class);
  }

  @Override
  @Nullable
  public ElixirEndEOL getEndEOL() {
    return findChildByClass(ElixirEndEOL.class);
  }

  @Override
  @Nullable
  public ElixirFnEOL getFnEOL() {
    return findChildByClass(ElixirFnEOL.class);
  }

  @Override
  @Nullable
  public ElixirList getList() {
    return findChildByClass(ElixirList.class);
  }

  @Override
  @Nullable
  public ElixirMap getMap() {
    return findChildByClass(ElixirMap.class);
  }

  @Override
  @Nullable
  public ElixirMaxExpression getMaxExpression() {
    return findChildByClass(ElixirMaxExpression.class);
  }

  @Override
  @Nullable
  public ElixirOpenParenthesis getOpenParenthesis() {
    return findChildByClass(ElixirOpenParenthesis.class);
  }

  @Override
  @Nullable
  public ElixirStab getStab() {
    return findChildByClass(ElixirStab.class);
  }

  @Override
  @Nullable
  public ElixirTuple getTuple() {
    return findChildByClass(ElixirTuple.class);
  }

  @Override
  @Nullable
  public ElixirUnaryOperatorEOL getUnaryOperatorEOL() {
    return findChildByClass(ElixirUnaryOperatorEOL.class);
  }

}
