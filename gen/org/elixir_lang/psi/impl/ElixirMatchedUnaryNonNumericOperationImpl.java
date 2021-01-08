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
import kotlin.ranges.IntRange;

public class ElixirMatchedUnaryNonNumericOperationImpl extends ElixirMatchedExpressionImpl implements ElixirMatchedUnaryNonNumericOperation {

  public ElixirMatchedUnaryNonNumericOperationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitMatchedUnaryNonNumericOperation(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirMatchedExpression getMatchedExpression() {
    return PsiTreeUtil.getChildOfType(this, ElixirMatchedExpression.class);
  }

  @Override
  @NotNull
  public ElixirUnaryPrefixOperator getUnaryPrefixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirUnaryPrefixOperator.class));
  }

  @Override
  @Nullable
  public String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @Override
  @NotNull
  public PsiElement functionNameElement() {
    return ElixirPsiImplUtil.functionNameElement(this);
  }

  @Override
  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  @Override
  @Nullable
  public String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  @Override
  @Nullable
  public PsiElement getNameIdentifier() {
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
  @Nullable
  public String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
  }

  @Override
  @Nullable
  public Quotable operand() {
    return ElixirPsiImplUtil.operand(this);
  }

  @Override
  @NotNull
  public Operator operator() {
    return ElixirPsiImplUtil.operator(this);
  }

  @Override
  @NotNull
  public PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @Override
  @Nullable
  public Integer primaryArity() {
    return ElixirPsiImplUtil.primaryArity(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  @Nullable
  public PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

  @Override
  @Nullable
  public Integer secondaryArity() {
    return ElixirPsiImplUtil.secondaryArity(this);
  }

  @Override
  public int resolvedFinalArity() {
    return ElixirPsiImplUtil.resolvedFinalArity(this);
  }

  @Override
  @NotNull
  public IntRange resolvedFinalArityRange() {
    return ElixirPsiImplUtil.resolvedFinalArityRange(this);
  }

  @Override
  @NotNull
  public String resolvedModuleName() {
    return ElixirPsiImplUtil.resolvedModuleName(this);
  }

  @Override
  @Nullable
  public Integer resolvedPrimaryArity() {
    return ElixirPsiImplUtil.resolvedPrimaryArity(this);
  }

  @Override
  @Nullable
  public Integer resolvedSecondaryArity() {
    return ElixirPsiImplUtil.resolvedSecondaryArity(this);
  }

  @Override
  @NotNull
  public PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
