// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirMatchedParenthesesArguments;
import org.elixir_lang.psi.ElixirMatchedUnqualifiedParenthesesCall;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirMatchedUnqualifiedParenthesesCallImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedUnqualifiedParenthesesCall {

  public ElixirMatchedUnqualifiedParenthesesCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedUnqualifiedParenthesesCall(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMatchedParenthesesArguments getMatchedParenthesesArguments() {
    return findNotNullChildByClass(ElixirMatchedParenthesesArguments.class);
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

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @NotNull
  public PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
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
