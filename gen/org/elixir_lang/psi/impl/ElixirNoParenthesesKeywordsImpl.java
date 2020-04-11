// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirNoParenthesesKeywordPair;
import org.elixir_lang.psi.ElixirNoParenthesesKeywords;
import org.elixir_lang.psi.ElixirVisitor;
import org.elixir_lang.psi.QuotableKeywordPair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirNoParenthesesKeywordsImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesKeywords {

  public ElixirNoParenthesesKeywordsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitNoParenthesesKeywords(this);
  }

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
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
