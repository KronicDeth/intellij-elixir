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

public class TupleImpl extends ASTWrapperPsiElement implements Tuple {

  public TupleImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitTuple(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public java.util.List<Term> getTermList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Term.class);
  }

}
