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
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirDecimalWholeNumberImpl extends ASTWrapperPsiElement implements ElixirDecimalWholeNumber {

  public ElixirDecimalWholeNumberImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitDecimalWholeNumber(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirDecimalDigits> getDecimalDigitsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirDecimalDigits.class);
  }

  @Override
  public int base() {
    return ElixirPsiImplUtil.base(this);
  }

  @Override
  public @NotNull List<Digits> digitsList() {
    return ElixirPsiImplUtil.digitsList(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
