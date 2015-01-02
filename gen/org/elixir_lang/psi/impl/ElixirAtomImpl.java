// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirAtom;
import org.elixir_lang.psi.ElixirCharList;
import org.elixir_lang.psi.ElixirString;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirAtomImpl extends ElixirMaxExpressionImpl implements ElixirAtom {

  public ElixirAtomImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitAtom(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCharList getCharList() {
    return findChildByClass(ElixirCharList.class);
  }

  @Override
  @Nullable
  public ElixirString getString() {
    return findChildByClass(ElixirString.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
