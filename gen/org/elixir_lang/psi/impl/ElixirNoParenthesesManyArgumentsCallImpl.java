// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirNoParenthesesManyArgumentsCallImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesManyArgumentsCall {

  public ElixirNoParenthesesManyArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesManyArgumentsCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArguments getNoParenthesesManyArguments() {
    return findChildByClass(ElixirNoParenthesesManyArguments.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier getNoParenthesesManyArgumentsUnqualifiedIdentifier() {
    return findChildByClass(ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesQualifiedIdentifier getNoParenthesesQualifiedIdentifier() {
    return findChildByClass(ElixirNoParenthesesQualifiedIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesStrict getNoParenthesesStrict() {
    return findChildByClass(ElixirNoParenthesesStrict.class);
  }

  @NotNull
  public QuotableArguments getArguments() {
    return ElixirPsiImplUtil.getArguments(this);
  }

  @NotNull
  public Quotable getIdentifier() {
    return ElixirPsiImplUtil.getIdentifier(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
