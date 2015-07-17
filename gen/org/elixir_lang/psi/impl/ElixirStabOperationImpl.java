// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirStabOperationImpl extends ASTWrapperPsiElement implements ElixirStabOperation {

  public ElixirStabOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStabOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirStabBody getStabBody() {
    return findChildByClass(ElixirStabBody.class);
  }

  @Override
  @NotNull
  public ElixirStabInfixOperator getStabInfixOperator() {
    return findNotNullChildByClass(ElixirStabInfixOperator.class);
  }

  @Override
  @Nullable
  public ElixirStabNoParenthesesSignature getStabNoParenthesesSignature() {
    return findChildByClass(ElixirStabNoParenthesesSignature.class);
  }

  @Override
  @Nullable
  public ElixirStabParenthesesSignature getStabParenthesesSignature() {
    return findChildByClass(ElixirStabParenthesesSignature.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
