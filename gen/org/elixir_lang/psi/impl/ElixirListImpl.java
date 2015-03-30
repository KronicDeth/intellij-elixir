// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirList;
import org.elixir_lang.psi.ElixirListKeywordPair;
import org.elixir_lang.psi.ElixirVisitor;
import org.elixir_lang.psi.KeywordPair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirListImpl extends ElixirMatchedExpressionImpl implements ElixirList {

  public ElixirListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitList(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirListKeywordPair> getListKeywordPairList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirListKeywordPair.class);
  }

  public List<KeywordPair> getKeywordPairList() {
    return ElixirPsiImplUtil.getKeywordPairList(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
