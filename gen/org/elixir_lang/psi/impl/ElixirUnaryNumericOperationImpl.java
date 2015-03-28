// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirUnaryNumericOperationImpl extends ASTWrapperPsiElement implements ElixirUnaryNumericOperation {

  public ElixirUnaryNumericOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnaryNumericOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirBinaryWholeNumber getBinaryWholeNumber() {
    return findChildByClass(ElixirBinaryWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirCharToken getCharToken() {
    return findChildByClass(ElixirCharToken.class);
  }

  @Override
  @Nullable
  public ElixirDecimalFloat getDecimalFloat() {
    return findChildByClass(ElixirDecimalFloat.class);
  }

  @Override
  @Nullable
  public ElixirDecimalWholeNumber getDecimalWholeNumber() {
    return findChildByClass(ElixirDecimalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirHexadecimalWholeNumber getHexadecimalWholeNumber() {
    return findChildByClass(ElixirHexadecimalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirOctalWholeNumber getOctalWholeNumber() {
    return findChildByClass(ElixirOctalWholeNumber.class);
  }

  @Override
  @NotNull
  public ElixirUnaryPrefixOperator getUnaryPrefixOperator() {
    return findNotNullChildByClass(ElixirUnaryPrefixOperator.class);
  }

  @Override
  @Nullable
  public ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber() {
    return findChildByClass(ElixirUnknownBaseWholeNumber.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
