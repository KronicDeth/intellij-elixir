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

public class ElixirInterpolatedStringHeredocLineImpl extends ASTWrapperPsiElement implements ElixirInterpolatedStringHeredocLine {

  public ElixirInterpolatedStringHeredocLineImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitInterpolatedStringHeredocLine(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEscapedEOL getEscapedEOL() {
    return PsiTreeUtil.getChildOfType(this, ElixirEscapedEOL.class);
  }

  @Override
  @NotNull
  public ElixirHeredocLinePrefix getHeredocLinePrefix() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirHeredocLinePrefix.class));
  }

  @Override
  @NotNull
  public ElixirInterpolatedStringHeredocLineBody getInterpolatedStringHeredocLineBody() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirInterpolatedStringHeredocLineBody.class));
  }

  @Override
  public Body getBody() {
    return ElixirPsiImplUtil.getBody(this);
  }

  @Override
  public @NotNull OtpErlangObject quote(@NotNull Heredoc heredoc, int prefixLength) {
    return ElixirPsiImplUtil.quote(this, heredoc, prefixLength);
  }

}
