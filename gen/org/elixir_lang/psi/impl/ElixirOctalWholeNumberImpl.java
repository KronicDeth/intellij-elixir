// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.Digits;
import org.elixir_lang.psi.ElixirOctalDigits;
import org.elixir_lang.psi.ElixirOctalWholeNumber;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirOctalWholeNumberImpl extends ElixirMatchedExpressionImpl implements ElixirOctalWholeNumber {

  public ElixirOctalWholeNumberImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitOctalWholeNumber(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirOctalDigits> getOctalDigitsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirOctalDigits.class);
  }

  @NotNull
  public int base() {
    return ElixirPsiImplUtil.base(this);
  }

  @NotNull
  public List<Digits> digitsList() {
    return ElixirPsiImplUtil.digitsList(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
