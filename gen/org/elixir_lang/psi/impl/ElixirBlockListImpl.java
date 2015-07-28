// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.ElixirBlockItem;
import org.elixir_lang.psi.ElixirBlockList;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirBlockListImpl extends ASTWrapperPsiElement implements ElixirBlockList {

  public ElixirBlockListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitBlockList(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirBlockItem> getBlockItemList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirBlockItem.class);
  }

  @NotNull
  public OtpErlangObject[] quoteArguments() {
    return ElixirPsiImplUtil.quoteArguments(this);
  }

}
