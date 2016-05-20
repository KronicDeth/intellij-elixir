// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirCharListLine;
import org.elixir_lang.psi.ElixirKeywordKey;
import org.elixir_lang.psi.ElixirStringLine;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirKeywordKeyImpl extends ASTWrapperPsiElement implements ElixirKeywordKey {

  public ElixirKeywordKeyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitKeywordKey(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCharListLine getCharListLine() {
    return findChildByClass(ElixirCharListLine.class);
  }

  @Override
  @Nullable
  public ElixirStringLine getStringLine() {
    return findChildByClass(ElixirStringLine.class);
  }

  @Nullable
  public String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  @Nullable
  public PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public PsiElement setName(String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
