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
import com.intellij.psi.PsiReference;

public class ElixirAtomImpl extends ASTWrapperPsiElement implements ElixirAtom {

  public ElixirAtomImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitAtom(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCharListLine getCharListLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirCharListLine.class);
  }

  @Override
  @Nullable
  public ElixirStringLine getStringLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirStringLine.class);
  }

  @Override
  public @Nullable PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
