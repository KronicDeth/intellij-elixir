// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirMultiplicationOperationImpl extends ASTWrapperPsiElement implements ElixirMultiplicationOperation {

  public ElixirMultiplicationOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMultiplicationOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirAtom> getAtomList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAtom.class);
  }

  @Override
  @NotNull
  public List<ElixirCharList> getCharListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharList.class);
  }

  @Override
  @NotNull
  public List<ElixirCharListHeredoc> getCharListHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharListHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirHatOperation> getHatOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirHatOperation.class);
  }

  @Override
  @Nullable
  public ElixirMultiplicationOperation getMultiplicationOperation() {
    return findChildByClass(ElixirMultiplicationOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirSigil> getSigilList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirSigil.class);
  }

  @Override
  @NotNull
  public List<ElixirString> getStringList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirString.class);
  }

  @Override
  @NotNull
  public List<ElixirStringHeredoc> getStringHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirUnaryOperation> getUnaryOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirUnaryOperation.class);
  }

}
