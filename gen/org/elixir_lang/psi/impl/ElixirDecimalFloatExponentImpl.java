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

public class ElixirDecimalFloatExponentImpl extends ASTWrapperPsiElement implements ElixirDecimalFloatExponent {

  public ElixirDecimalFloatExponentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitDecimalFloatExponent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirDecimalFloatExponentSign getDecimalFloatExponentSign() {
    return PsiTreeUtil.getChildOfType(this, ElixirDecimalFloatExponentSign.class);
  }

  @Override
  @NotNull
  public ElixirDecimalWholeNumber getDecimalWholeNumber() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDecimalWholeNumber.class));
  }

}
