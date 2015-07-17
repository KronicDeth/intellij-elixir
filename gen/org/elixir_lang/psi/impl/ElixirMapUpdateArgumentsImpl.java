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

public class ElixirMapUpdateArgumentsImpl extends ASTWrapperPsiElement implements ElixirMapUpdateArguments {

  public ElixirMapUpdateArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMapUpdateArguments(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAssociations getAssociations() {
    return findChildByClass(ElixirAssociations.class);
  }

  @Override
  @Nullable
  public ElixirAssociationsBase getAssociationsBase() {
    return findChildByClass(ElixirAssociationsBase.class);
  }

  @Override
  @Nullable
  public ElixirKeywords getKeywords() {
    return findChildByClass(ElixirKeywords.class);
  }

  @Override
  @NotNull
  public ElixirMatchedMatchOperation getMatchedMatchOperation() {
    return findNotNullChildByClass(ElixirMatchedMatchOperation.class);
  }

  @Override
  @NotNull
  public ElixirPipeInfixOperator getPipeInfixOperator() {
    return findNotNullChildByClass(ElixirPipeInfixOperator.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
