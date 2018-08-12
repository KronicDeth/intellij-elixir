// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirRelativeIdentifierImpl extends ASTWrapperPsiElement implements ElixirRelativeIdentifier {

  public ElixirRelativeIdentifierImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitRelativeIdentifier(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtomKeyword getAtomKeyword() {
    return PsiTreeUtil.getChildOfType(this, ElixirAtomKeyword.class);
  }

  @Override
  @Nullable
  public ElixirCharListLine getCharListLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirCharListLine.class);
  }

  @Override
  @Nullable
  public ElixirStringLine getStringLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirStringLine.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
