// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.stub.UnqualifiedNoParenthesesManyArgumentsCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl extends NamedStubbedPsiElementBase<UnqualifiedNoParenthesesManyArgumentsCall> implements ElixirUnqualifiedNoParenthesesManyArgumentsCall {

  public ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl(UnqualifiedNoParenthesesManyArgumentsCall stub, IStubElementType type) {
    super(stub, type);
  }

  public ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitUnqualifiedNoParenthesesManyArgumentsCall(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirEmptyParentheses> getEmptyParenthesesList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirIdentifier getIdentifier() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, ElixirIdentifier.class));
  }

  @Override
  @NotNull
  public List<ElixirMatchedExpression> getMatchedExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesKeywords getNoParenthesesKeywords() {
    return PsiTreeUtil.getChildOfType(this, ElixirNoParenthesesKeywords.class);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesManyStrictNoParenthesesExpression> getNoParenthesesManyStrictNoParenthesesExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesManyStrictNoParenthesesExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesStrict getNoParenthesesStrict() {
    return PsiTreeUtil.getChildOfType(this, ElixirNoParenthesesStrict.class);
  }

  @Nullable
  public String canonicalName() {
    return ElixirPsiImplUtil.canonicalName(this);
  }

  @NotNull
  public Set<String> canonicalNameSet() {
    return ElixirPsiImplUtil.canonicalNameSet(this);
  }

  @Nullable
  public String functionName() {
    return ElixirPsiImplUtil.functionName(this);
  }

  @NotNull
  public PsiElement functionNameElement() {
    return ElixirPsiImplUtil.functionNameElement(this);
  }

  @Nullable
  public ElixirDoBlock getDoBlock() {
    return ElixirPsiImplUtil.getDoBlock(this);
  }

  public boolean hasDoBlockOrKeyword() {
    return ElixirPsiImplUtil.hasDoBlockOrKeyword(this);
  }

  @Nullable
  public String getName() {
    return ElixirPsiImplUtil.getName(this);
  }

  public PsiElement getNameIdentifier() {
    return ElixirPsiImplUtil.getNameIdentifier(this);
  }

  @NotNull
  public ItemPresentation getPresentation() {
    return ElixirPsiImplUtil.getPresentation(this);
  }

  @Nullable
  public PsiReference getReference() {
    return ElixirPsiImplUtil.getReference(this);
  }

  public boolean isCalling(String resolvedModuleName, String functionName) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, functionName);
  }

  public boolean isCalling(String resolvedModuleName, String functionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCalling(this, resolvedModuleName, functionName, resolvedFinalArity);
  }

  public boolean isCallingMacro(String resolvedModuleName, String functionName) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, functionName);
  }

  public boolean isCallingMacro(String resolvedModuleName, String functionName, int resolvedFinalArity) {
    return ElixirPsiImplUtil.isCallingMacro(this, resolvedModuleName, functionName, resolvedFinalArity);
  }

  @Nullable
  public String moduleName() {
    return ElixirPsiImplUtil.moduleName(this);
  }

  @NotNull
  public PsiElement[] primaryArguments() {
    return ElixirPsiImplUtil.primaryArguments(this);
  }

  @Nullable
  public Integer primaryArity() {
    return ElixirPsiImplUtil.primaryArity(this);
  }

  public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) {
    return ElixirPsiImplUtil.processDeclarations(this, processor, state, lastParent, place);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public int resolvedFinalArity() {
    return ElixirPsiImplUtil.resolvedFinalArity(this);
  }

  @NotNull
  public IntRange resolvedFinalArityRange() {
    return ElixirPsiImplUtil.resolvedFinalArityRange(this);
  }

  @NotNull
  public String resolvedModuleName() {
    return ElixirPsiImplUtil.resolvedModuleName(this);
  }

  @Nullable
  public Integer resolvedPrimaryArity() {
    return ElixirPsiImplUtil.resolvedPrimaryArity(this);
  }

  @Nullable
  public Integer resolvedSecondaryArity() {
    return ElixirPsiImplUtil.resolvedSecondaryArity(this);
  }

  @Nullable
  public PsiElement[] secondaryArguments() {
    return ElixirPsiImplUtil.secondaryArguments(this);
  }

  @Nullable
  public Integer secondaryArity() {
    return ElixirPsiImplUtil.secondaryArity(this);
  }

  @NotNull
  public PsiElement setName(String newName) {
    return ElixirPsiImplUtil.setName(this, newName);
  }

}
