// This is a generated file. Not intended for manual editing.
package org.elixir_lang.heex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.heex.psi.Types.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.heex.psi.*;

public class HEExBracesImpl extends ASTWrapperPsiElement implements HEExBraces {

  public HEExBracesImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull HEExVisitor visitor) {
    visitor.visitBraces(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof HEExVisitor) accept((HEExVisitor)visitor);
    else super.accept(visitor);
  }

}
