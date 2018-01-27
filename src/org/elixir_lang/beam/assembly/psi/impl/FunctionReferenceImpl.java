// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.beam.assembly.psi.Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.beam.assembly.psi.*;

public class FunctionReferenceImpl extends ASTWrapperPsiElement implements FunctionReference {

  public FunctionReferenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitFunctionReference(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public Qualifier getQualifier() {
    return findNotNullChildByClass(Qualifier.class);
  }

}
