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
