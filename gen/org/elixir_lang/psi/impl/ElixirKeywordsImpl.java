// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirKeywordPair;
import org.elixir_lang.psi.ElixirKeywords;
import org.elixir_lang.psi.ElixirVisitor;
import org.elixir_lang.psi.QuotableKeywordPair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirKeywordsImpl extends ASTWrapperPsiElement implements ElixirKeywords {

  public ElixirKeywordsImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitKeywords(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirKeywordPair> getKeywordPairList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirKeywordPair.class);
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
