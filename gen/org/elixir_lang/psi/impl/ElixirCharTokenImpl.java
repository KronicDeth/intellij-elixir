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

public class ElixirCharTokenImpl extends ASTWrapperPsiElement implements ElixirCharToken {

  public ElixirCharTokenImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitCharToken(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEscapedCharacter getEscapedCharacter() {
    return PsiTreeUtil.getChildOfType(this, ElixirEscapedCharacter.class);
  }

  @Override
  @Nullable
  public ElixirEscapedEOL getEscapedEOL() {
    return PsiTreeUtil.getChildOfType(this, ElixirEscapedEOL.class);
  }

  @Override
  @Nullable
  public ElixirQuoteHexadecimalEscapeSequence getQuoteHexadecimalEscapeSequence() {
    return PsiTreeUtil.getChildOfType(this, ElixirQuoteHexadecimalEscapeSequence.class);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
