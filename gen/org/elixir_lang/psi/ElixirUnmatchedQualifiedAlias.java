// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirUnmatchedQualifiedAlias extends ElixirUnmatchedExpression, QualifiedAlias {

  @NotNull
  ElixirAlias getAlias();

  @NotNull
  ElixirDotInfixOperator getDotInfixOperator();

  @NotNull
  ElixirUnmatchedExpression getUnmatchedExpression();

  @Nullable
  String fullyQualifiedName();

  @NotNull
  String getName();

  @Nullable
  PsiElement getNameIdentifier();

  @Nullable
  ItemPresentation getPresentation();

  @Nullable
  PsiReference getReference();

  @Nullable
  PsiPolyVariantReference getReference(@NotNull PsiElement maxScope);

  boolean isModuleName();

  boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place);

  @NotNull
  OtpErlangObject quote();

  @NotNull
  PsiElement setName(@NotNull String newName);

}
