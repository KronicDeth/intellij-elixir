// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirAnonymousFunction;
import org.elixir_lang.psi.ElixirEndOfExpression;
import org.elixir_lang.psi.ElixirStab;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirAnonymousFunctionImpl extends ASTWrapperPsiElement implements ElixirAnonymousFunction {

  public ElixirAnonymousFunctionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAnonymousFunction(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirEndOfExpression> getEndOfExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEndOfExpression.class);
  }

  @Override
  @NotNull
  public ElixirStab getStab() {
    return findNotNullChildByClass(ElixirStab.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
