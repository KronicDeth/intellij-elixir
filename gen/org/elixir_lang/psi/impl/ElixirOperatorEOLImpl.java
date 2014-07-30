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

public class ElixirOperatorEOLImpl extends ASTWrapperPsiElement implements ElixirOperatorEOL {

  public ElixirOperatorEOLImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitOperatorEOL(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAddOperatorEOL getAddOperatorEOL() {
    return findChildByClass(ElixirAddOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirAndOperatorEOL getAndOperatorEOL() {
    return findChildByClass(ElixirAndOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirArrowOperatorEOL getArrowOperatorEOL() {
    return findChildByClass(ElixirArrowOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirCompOperatorEOL getCompOperatorEOL() {
    return findChildByClass(ElixirCompOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirHatOperatorEOL getHatOperatorEOL() {
    return findChildByClass(ElixirHatOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirInMatchOperatorEOL getInMatchOperatorEOL() {
    return findChildByClass(ElixirInMatchOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirInOperatorEOL getInOperatorEOL() {
    return findChildByClass(ElixirInOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirMatchOperatorEOL getMatchOperatorEOL() {
    return findChildByClass(ElixirMatchOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirMultiplyOperatorEOL getMultiplyOperatorEOL() {
    return findChildByClass(ElixirMultiplyOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirOrOperatorEOL getOrOperatorEOL() {
    return findChildByClass(ElixirOrOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirPipeOperatorEOL getPipeOperatorEOL() {
    return findChildByClass(ElixirPipeOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirRelOperatorEOL getRelOperatorEOL() {
    return findChildByClass(ElixirRelOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirTwoOperatorEOL getTwoOperatorEOL() {
    return findChildByClass(ElixirTwoOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirTypeOperatorEOL getTypeOperatorEOL() {
    return findChildByClass(ElixirTypeOperatorEOL.class);
  }

  @Override
  @Nullable
  public ElixirWhenOperatorEOL getWhenOperatorEOL() {
    return findChildByClass(ElixirWhenOperatorEOL.class);
  }

}
