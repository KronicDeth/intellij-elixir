// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirCharListHeredocLine;
import org.elixir_lang.psi.ElixirCharListHeredocLineWhitespace;
import org.elixir_lang.psi.ElixirInterpolatedCharListBody;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirCharListHeredocLineImpl extends ASTWrapperPsiElement implements ElixirCharListHeredocLine {

  public ElixirCharListHeredocLineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCharListHeredocLine(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirCharListHeredocLineWhitespace getCharListHeredocLineWhitespace() {
    return findNotNullChildByClass(ElixirCharListHeredocLineWhitespace.class);
  }

  @Override
  @NotNull
  public ElixirInterpolatedCharListBody getInterpolatedCharListBody() {
    return findNotNullChildByClass(ElixirInterpolatedCharListBody.class);
  }

  @NotNull
  public OtpErlangObject quote(int prefixLength) {
    return ElixirPsiImplUtil.quote(this, prefixLength);
  }

}
