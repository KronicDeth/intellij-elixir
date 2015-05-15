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

public class ElixirCharTokenImpl extends ASTWrapperPsiElement implements ElixirCharToken {

  public ElixirCharTokenImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCharToken(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirEscapedCharacter getEscapedCharacter() {
    return findChildByClass(ElixirEscapedCharacter.class);
  }

  @Override
  @Nullable
  public ElixirEscapedEOL getEscapedEOL() {
    return findChildByClass(ElixirEscapedEOL.class);
  }

  @Override
  @Nullable
  public ElixirQuoteHexadecimalEscapeSequence getQuoteHexadecimalEscapeSequence() {
    return findChildByClass(ElixirQuoteHexadecimalEscapeSequence.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
