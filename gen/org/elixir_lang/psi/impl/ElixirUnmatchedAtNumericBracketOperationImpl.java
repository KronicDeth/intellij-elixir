// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirUnmatchedAtNumericBracketOperationImpl extends ElixirUnmatchedExpressionImpl implements ElixirUnmatchedAtNumericBracketOperation {

  public ElixirUnmatchedAtNumericBracketOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedAtNumericBracketOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirAtPrefixOperator getAtPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirAtPrefixOperator.class));
  }

  @Override
  @Nullable
  public ElixirBinaryWholeNumber getBinaryWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirBinaryWholeNumber.class);
  }

  @Override
  @NotNull
  public ElixirBracketArguments getBracketArguments() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirBracketArguments.class));
  }

  @Override
  @Nullable
  public ElixirCharToken getCharToken() {
    return PsiTreeUtil.getChildOfType(this, ElixirCharToken.class);
  }

  @Override
  @Nullable
  public ElixirDecimalFloat getDecimalFloat() {
    return PsiTreeUtil.getChildOfType(this, ElixirDecimalFloat.class);
  }

  @Override
  @Nullable
  public ElixirDecimalWholeNumber getDecimalWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirDecimalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirHexadecimalWholeNumber getHexadecimalWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirHexadecimalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirOctalWholeNumber getOctalWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirOctalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirUnknownBaseWholeNumber.class);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
