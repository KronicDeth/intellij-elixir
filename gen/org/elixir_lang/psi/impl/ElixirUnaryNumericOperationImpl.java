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
import kotlin.ranges.IntRange;

public class ElixirUnaryNumericOperationImpl extends ASTWrapperPsiElement implements ElixirUnaryNumericOperation {

  public ElixirUnaryNumericOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnaryNumericOperation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirBinaryWholeNumber getBinaryWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirBinaryWholeNumber.class);
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
  @NotNull
  public ElixirUnaryPrefixOperator getUnaryPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirUnaryPrefixOperator.class));
  }

  @Override
  @Nullable
  public ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirUnknownBaseWholeNumber.class);
  }

  @Override
  public @Nullable String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @Override
  public @NotNull PsiElement functionNameElement() {
    return ElixirPsiImplUtil.functionNameElement(this);
  }

  @Override
  public @Nullable ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @Override
  public @Nullable String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public boolean hasDoBlockOrKeyword() {
    return ElixirPsiImplUtil.hasDoBlockOrKeyword(this);
  }

  @Override
  public boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, functionName);
  }

  @Override
  public boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, functionName, resolvedFinalArity);
  }

  @Override
  public boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, functionName);
  }

  @Override
  public boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, functionName, resolvedFinalArity);
  }

  @Override
  public @Nullable String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
  }

  @Override
  public @Nullable Quotable operand() {
    return ElixirPsiImplUtil.operand(this);
  }

  @Override
  public @NotNull Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  @Override
  public @NotNull PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @Override
  public @Nullable Integer primaryArity() {
    return ElixirPsiImplUtil.primaryArity(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  public @Nullable PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

  @Override
  public @Nullable Integer secondaryArity() {
    return ElixirPsiImplUtil.secondaryArity(this);
  }

  @Override
  public int resolvedFinalArity() {
    return ElixirPsiImplUtil.resolvedFinalArity(this);
  }

  @Override
  public @NotNull IntRange resolvedFinalArityRange() {
    return ElixirPsiImplUtil.resolvedFinalArityRange(this);
  }

  @Override
  public @NotNull String resolvedModuleName() {
    return ElixirPsiImplUtil.resolvedModuleName(this);
  }

  @Override
  public @Nullable Integer resolvedPrimaryArity() {
    return ElixirPsiImplUtil.resolvedPrimaryArity(this);
  }

  @Override
  public @Nullable Integer resolvedSecondaryArity() {
    return ElixirPsiImplUtil.resolvedSecondaryArity(this);
  }

  @Override
  public @NotNull PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
