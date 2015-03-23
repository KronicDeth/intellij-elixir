// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirMatchedNonNumericUnaryRightOperationImpl extends ASTWrapperPsiElement implements ElixirMatchedNonNumericUnaryRightOperation {

  public ElixirMatchedNonNumericUnaryRightOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedNonNumericUnaryRightOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchedNonNumericCaptureRightOperation getMatchedNonNumericCaptureRightOperation() {
    return findChildByClass(ElixirMatchedNonNumericCaptureRightOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedNonNumericUnaryRightOperation getMatchedNonNumericUnaryRightOperation() {
    return findChildByClass(ElixirMatchedNonNumericUnaryRightOperation.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArgumentsCall getNoParenthesesManyArgumentsCall() {
    return findChildByClass(ElixirNoParenthesesManyArgumentsCall.class);
  }

  @Override
  @NotNull
  public ElixirUnaryPrefixOperator getUnaryPrefixOperator() {
    return findNotNullChildByClass(ElixirUnaryPrefixOperator.class);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
