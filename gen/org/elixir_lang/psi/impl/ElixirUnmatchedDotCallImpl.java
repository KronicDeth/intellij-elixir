// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.stub.UnmatchedDotCall;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import java.util.Set;
import com.intellij.psi.stubs.IStubElementType;

public class ElixirUnmatchedDotCallImpl extends NamedStubbedPsiElementBase<UnmatchedDotCall> implements ElixirUnmatchedDotCall {

  public ElixirUnmatchedDotCallImpl(@NotNull UnmatchedDotCall stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public ElixirUnmatchedDotCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedDotCall(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirDoBlock getDoBlock() {
    return PsiTreeUtil.getChildOfType(this, ElixirDoBlock.class);
  }

  @Override
  @NotNull
  public ElixirDotInfixOperator getDotInfixOperator() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirDotInfixOperator.class));
  }

  @Override
  @NotNull
  public List<ElixirParenthesesArguments> getParenthesesArgumentsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirParenthesesArguments.class);
  }

  @Override
  @NotNull
  public ElixirUnmatchedExpression getUnmatchedExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirUnmatchedExpression.class));
  }

  @Override
  public @Nullable String getCanonicalName() {
    return ElixirPsiImplUtil.getCanonicalName(this);
  }

  @Override
  public @NotNull Set<String> getCanonicalNameSet() {
    return ElixirPsiImplUtil.getCanonicalNameSet(this);
  }

  @Override
  public @Nullable String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @Override
  public @Nullable PsiElement functionNameElement() {
    return ElixirPsiImplUtil.functionNameElement(this);
  }

  @Override
  public boolean hasDoBlockOrKeyword() {
    return ElixirPsiImplUtil.hasDoBlockOrKeyword(this);
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
  public @NotNull ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  @Override
  public @Nullable PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
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
  public @Nullable String implementedProtocolName() {
    return ElixirPsiImplUtil.implementedProtocolName(this);
  }

  @Override
  public @Nullable String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
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
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  public int resolvedFinalArity() {
    return ElixirPsiImplUtil.resolvedFinalArity(this);
  }

  @Override
  public @NotNull ArityInterval resolvedFinalArityInterval() {
    return ElixirPsiImplUtil.resolvedFinalArityInterval(this);
  }

  @Override
  public @Nullable String resolvedModuleName() {
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
  public @Nullable PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

  @Override
  public @Nullable Integer secondaryArity() {
    return ElixirPsiImplUtil.secondaryArity(this);
  }

  @Override
  public @NotNull PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
