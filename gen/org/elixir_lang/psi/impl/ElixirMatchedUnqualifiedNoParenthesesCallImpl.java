// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirMatchedUnqualifiedNoParenthesesCallImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedUnqualifiedNoParenthesesCall {

  public ElixirMatchedUnqualifiedNoParenthesesCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedUnqualifiedNoParenthesesCall(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesOneArgument getNoParenthesesOneArgument() {
    return findNotNullChildByClass(ElixirNoParenthesesOneArgument.class);
  }

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
