// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.beam.assembly.psi.Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.beam.assembly.psi.*;

public class TermImpl extends ASTWrapperPsiElement implements Term {

  public TermImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitTerm(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BitString getBitString() {
    return findChildByClass(BitString.class);
  }

  @Override
  @Nullable
  public FunctionReference getFunctionReference() {
    return findChildByClass(FunctionReference.class);
  }

  @Override
  @Nullable
  public List getList() {
    return findChildByClass(List.class);
  }

  @Override
  @Nullable
  public Map getMap() {
    return findChildByClass(Map.class);
  }

  @Override
  @Nullable
  public Struct getStruct() {
    return findChildByClass(Struct.class);
  }

  @Override
  @Nullable
  public Tuple getTuple() {
    return findChildByClass(Tuple.class);
  }

  @Override
  @Nullable
  public Values getValues() {
    return findChildByClass(Values.class);
  }

}
