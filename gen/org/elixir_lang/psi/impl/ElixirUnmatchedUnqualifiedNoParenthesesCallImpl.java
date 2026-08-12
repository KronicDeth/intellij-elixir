// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import org.elixir_lang.psi.stub.UnmatchedUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import java.util.Set;
import com.intellij.psi.stubs.IStubElementType;

public class ElixirUnmatchedUnqualifiedNoParenthesesCallImpl extends NamedStubbedPsiElementBase<UnmatchedUnqualifiedNoParenthesesCall> implements ElixirUnmatchedUnqualifiedNoParenthesesCall {

  public ElixirUnmatchedUnqualifiedNoParenthesesCallImpl(@NotNull UnmatchedUnqualifiedNoParenthesesCall stub, @NotNull IStubElementType type) {
    super(stub, type);
  }

  public ElixirUnmatchedUnqualifiedNoParenthesesCallImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnmatchedUnqualifiedNoParenthesesCall(this);
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
  public ElixirIdentifier getIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirIdentifier.class));
  }

  @Override
  @NotNull
  public ElixirNoParenthesesOneArgument getNoParenthesesOneArgument() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirNoParenthesesOneArgument.class));
  }

  @Override
  @RequiresReadLock
  public @Nullable String canonicalName() {
    return ElixirPsiImplUtil.canonicalName(this);
  }

  @Override
  @RequiresReadLock
  public @NotNull Set<String> canonicalNameSet() {
    return ElixirPsiImplUtil.canonicalNameSet(this);
  }

  @Override
  @RequiresReadLock
  public int exportedArity(@NotNull ResolveState state) {
    return ElixirPsiImplUtil.exportedArity(this, state);
  }

  @Override
  @RequiresReadLock
  public @Nullable String exportedName() {
    return ElixirPsiImplUtil.exportedName(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @Override
  @RequiresReadLock
  public @NotNull PsiElement functionNameElement() {
    return ElixirPsiImplUtil.functionNameElement(this);
  }

  @Override
  @RequiresReadLock
  public boolean hasDoBlockOrKeyword() {
    return ElixirPsiImplUtil.hasDoBlockOrKeyword(this);
  }

  @Override
  @RequiresReadLock
  public boolean isExported() {
    return ElixirPsiImplUtil.isExported(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  @RequiresReadLock
  public @NotNull ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  @Override
  public @Nullable PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  @Override
  @RequiresReadLock
  public boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, functionName);
  }

  @Override
  @RequiresReadLock
  public boolean isCalling(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, functionName, resolvedFinalArity);
  }

  @Override
  @RequiresReadLock
  public boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, functionName);
  }

  @Override
  @RequiresReadLock
  public boolean isCallingMacro(@NotNull String resolvedModuleName, @NotNull String functionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, functionName, resolvedFinalArity);
  }

  @Override
  public @Nullable String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
  }

  @Override
  @RequiresReadLock
  public @NotNull PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable Integer primaryArity() {
    return ElixirPsiImplUtil.primaryArity(this);
  }

  @Override
  @RequiresReadLock
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @Override
  @RequiresReadLock
  public @Nullable String implementedProtocolName() {
    return ElixirPsiImplUtil.implementedProtocolName(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  @RequiresReadLock
  public int resolvedFinalArity() {
    return ElixirPsiImplUtil.resolvedFinalArity(this);
  }

  @Override
  @RequiresReadLock
  public @NotNull ArityInterval resolvedFinalArityInterval() {
    return ElixirPsiImplUtil.resolvedFinalArityInterval(this);
  }

  @Override
  public @NotNull String resolvedModuleName() {
    return ElixirPsiImplUtil.resolvedModuleName(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable Integer resolvedPrimaryArity() {
    return ElixirPsiImplUtil.resolvedPrimaryArity(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable Integer resolvedSecondaryArity() {
    return ElixirPsiImplUtil.resolvedSecondaryArity(this);
  }

  @Override
  public @Nullable PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

  @Override
  @RequiresReadLock
  public @Nullable Integer secondaryArity() {
    return ElixirPsiImplUtil.secondaryArity(this);
  }

  @Override
  public @NotNull PsiElement setName(@NotNull String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
