// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl extends ASTWrapperPsiElement implements ElixirUnqualifiedNoParenthesesManyArgumentsCall {

  public ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnqualifiedNoParenthesesManyArgumentsCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArguments getNoParenthesesManyArguments() {
    return findChildByClass(ElixirNoParenthesesManyArguments.class);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier getNoParenthesesManyArgumentsUnqualifiedIdentifier() {
    return findNotNullChildByClass(ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesStrict getNoParenthesesStrict() {
    return findChildByClass(ElixirNoParenthesesStrict.class);
  }

  @Nullable
  public String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @NotNull
  public ASTNode functionNameNode() {
    return ElixirPsiImplUtil.functionNameNode(this);
  }

  @Nullable
  public String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
  }

  @NotNull
  public QuotableArguments getArguments() {
    return ElixirPsiImplUtil.getArguments(this);
  }

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @NotNull
  public Quotable getIdentifier() {
    return ElixirPsiImplUtil.getIdentifier(this);
  }

  @Nullable
  public PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public OtpErlangObject[] quoteArguments() {
    return ElixirPsiImplUtil.quoteArguments(this);
  }

  public OtpErlangObject quoteIdentifier() {
    return ElixirPsiImplUtil.quoteIdentifier(this);
  }

  @NotNull
  public String resolvedFunctionName() {
    return ElixirPsiImplUtil.resolvedFunctionName(this);
  }

  @NotNull
  public String resolvedModuleName() {
    return ElixirPsiImplUtil.resolvedModuleName(this);
  }

  @Nullable
  public PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

}
