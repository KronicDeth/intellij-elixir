// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.Digits;
import org.elixir_lang.psi.ElixirHexadecimalDigits;
import org.elixir_lang.psi.ElixirHexadecimalWholeNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirHexadecimalWholeNumberImpl extends ASTWrapperPsiElement implements ElixirHexadecimalWholeNumber {

  public ElixirHexadecimalWholeNumberImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitHexadecimalWholeNumber(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirHexadecimalDigits> getHexadecimalDigitsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirHexadecimalDigits.class);
  }

  @Override
  public int base() {
    return ElixirPsiImplUtil.base(this);
  }

  @Override
  @NotNull
  public List<Digits> digitsList() {
    return ElixirPsiImplUtil.digitsList(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
