// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirMapArguments;
import org.elixir_lang.psi.ElixirMapConstructionArguments;
import org.elixir_lang.psi.ElixirMapUpdateArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirMapArgumentsImpl extends ASTWrapperPsiElement implements ElixirMapArguments {

  public ElixirMapArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMapArguments(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMapConstructionArguments getMapConstructionArguments() {
    return findChildByClass(ElixirMapConstructionArguments.class);
  }

  @Override
  @Nullable
  public ElixirMapUpdateArguments getMapUpdateArguments() {
    return findChildByClass(ElixirMapUpdateArguments.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
