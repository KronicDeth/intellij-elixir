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

public class ElixirMapConstructionArgumentsImpl extends ASTWrapperPsiElement implements ElixirMapConstructionArguments {

  public ElixirMapConstructionArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMapConstructionArguments(this);
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

  @NotNull
  public OtpErlangObject[] quoteArguments() {
    return ElixirPsiImplUtil.quoteArguments(this);
  }

}
