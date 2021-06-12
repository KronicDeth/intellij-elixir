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

public class ElixirNoParenthesesKeywordsImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesKeywords {

  public ElixirNoParenthesesKeywordsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitNoParenthesesKeywords(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesKeywordPair> getNoParenthesesKeywordPairList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesKeywordPair.class);
  }

  @Override
  public List<QuotableKeywordPair> quotableKeywordPairList() {
    return ElixirPsiImplUtil.quotableKeywordPairList(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
