// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.elixir_lang.psi.ElixirDoBlock;
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument;
import org.elixir_lang.psi.ElixirUnmatchedUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnmatchedUnqualifiedNoParenthesesCallImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedUnqualifiedNoParenthesesCall {

  public ElixirUnmatchedUnqualifiedNoParenthesesCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnmatchedUnqualifiedNoParenthesesCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirDoBlock getDoBlock() {
    return findChildByClass(ElixirDoBlock.class);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesOneArgument getNoParenthesesOneArgument() {
    return findNotNullChildByClass(ElixirNoParenthesesOneArgument.class);
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

  public boolean isDefmodule() {
    return ElixirPsiImplUtil.isDefmodule(this);
  }

  @NotNull
  public PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
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
