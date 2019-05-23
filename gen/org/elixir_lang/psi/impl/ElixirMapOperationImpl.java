// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirMapArguments;
import org.elixir_lang.psi.ElixirMapOperation;
import org.elixir_lang.psi.ElixirMapPrefixOperator;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirMapOperationImpl extends ASTWrapperPsiElement implements ElixirMapOperation {

  public ElixirMapOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMapOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirMapArguments getMapArguments() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMapArguments.class));
  }

  @Override
  @NotNull
  public ElixirMapPrefixOperator getMapPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirMapPrefixOperator.class));
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
