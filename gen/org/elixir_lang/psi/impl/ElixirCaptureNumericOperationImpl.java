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

public class ElixirCaptureNumericOperationImpl extends ASTWrapperPsiElement implements ElixirCaptureNumericOperation {

  public ElixirCaptureNumericOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCaptureNumericOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirBinaryWholeNumber getBinaryWholeNumber() {
    return findChildByClass(ElixirBinaryWholeNumber.class);
  }

  @Override
  @NotNull
  public ElixirCapturePrefixOperator getCapturePrefixOperator() {
    return findNotNullChildByClass(ElixirCapturePrefixOperator.class);
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
  @Nullable
  public ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber() {
    return findChildByClass(ElixirUnknownBaseWholeNumber.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
