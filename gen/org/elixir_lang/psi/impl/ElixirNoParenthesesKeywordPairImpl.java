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

public class ElixirNoParenthesesKeywordPairImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesKeywordPair {

  public ElixirNoParenthesesKeywordPairImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesKeywordPair(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirKeywordKey getKeywordKey() {
    return findNotNullChildByClass(ElixirKeywordKey.class);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesExpression getNoParenthesesExpression() {
    return findNotNullChildByClass(ElixirNoParenthesesExpression.class);
  }

  public Quotable getKeywordValue() {
    return ElixirPsiImplUtil.getKeywordValue(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
