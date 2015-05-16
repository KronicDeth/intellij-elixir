// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirCharTokenImpl extends ASTWrapperPsiElement implements ElixirCharToken {

  public ElixirCharTokenImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCharToken(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEscapedCharacter getEscapedCharacter() {
    return findChildByClass(ElixirEscapedCharacter.class);
  }

  @Override
  @Nullable
  public ElixirEscapedEOL getEscapedEOL() {
    return findChildByClass(ElixirEscapedEOL.class);
  }

  @Override
  @Nullable
  public ElixirQuoteHexadecimalEscapeSequence getQuoteHexadecimalEscapeSequence() {
    return findChildByClass(ElixirQuoteHexadecimalEscapeSequence.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
