// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirBlockIdentifier;
import org.elixir_lang.psi.ElixirBlockItem;
import org.elixir_lang.psi.ElixirEndOfExpression;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirBlockItemImpl extends ASTWrapperPsiElement implements ElixirBlockItem {

  public ElixirBlockItemImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBlockItem(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirBlockIdentifier getBlockIdentifier() {
    return findNotNullChildByClass(ElixirBlockIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirEndOfExpression getEndOfExpression() {
    return findChildByClass(ElixirEndOfExpression.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
