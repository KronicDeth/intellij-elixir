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
import com.intellij.psi.tree.IElementType;

public class ElixirInterpolatedCharListHeredocLineImpl extends ASTWrapperPsiElement implements ElixirInterpolatedCharListHeredocLine {

  public ElixirInterpolatedCharListHeredocLineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitInterpolatedCharListHeredocLine(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirHeredocLinePrefix getHeredocLinePrefix() {
    return findNotNullChildByClass(ElixirHeredocLinePrefix.class);
  }

  @Override
  @NotNull
  public ElixirInterpolatedCharListBody getInterpolatedCharListBody() {
    return findNotNullChildByClass(ElixirInterpolatedCharListBody.class);
  }

  public IElementType getFragmentType() {
    return ElixirPsiImplUtil.getFragmentType(this);
  }

  public InterpolatedBody getInterpolatedBody() {
    return ElixirPsiImplUtil.getInterpolatedBody(this);
  }

  @NotNull
  public OtpErlangObject quote(Heredoc heredoc, int prefixLength) {
    return ElixirPsiImplUtil.quote(this, heredoc, prefixLength);
  }

}
