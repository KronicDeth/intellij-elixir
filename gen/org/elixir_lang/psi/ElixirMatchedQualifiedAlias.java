// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.elixir_lang.psi.stub.MatchedQualifiedAlias;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedQualifiedAlias extends ElixirMatchedExpression, NamedElement, QualifiedAlias, StubBasedPsiElement<MatchedQualifiedAlias> {

  @NotNull
  ElixirAlias getAlias();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  String fullyQualifiedName();

  @NotNull
  String getName();

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  PsiReference getReference();

  boolean isModuleName();

  boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place);

  @NotNull
  OtpErlangObject quote();

  @NotNull
  PsiElement setName(String newName);

}
