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

public class ElixirCharListHeredocImpl extends ASTWrapperPsiElement implements ElixirCharListHeredoc {

  public ElixirCharListHeredocImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCharListHeredoc(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirCharListHeredocLine> getCharListHeredocLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharListHeredocLine.class);
  }

  @Override
  @NotNull
  public ElixirCharListHeredocPrefix getCharListHeredocPrefix() {
    return findNotNullChildByClass(ElixirCharListHeredocPrefix.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
