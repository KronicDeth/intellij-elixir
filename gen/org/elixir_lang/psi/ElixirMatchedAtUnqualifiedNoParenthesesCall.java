// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirMatchedAtUnqualifiedNoParenthesesCall extends ElixirMatchedExpression, AtUnqualifiedNoParenthesesCall, MatchedCall {

  @NotNull
  ElixirAtIdentifier getAtIdentifier();

  @NotNull
  ElixirNoParenthesesOneArgument getNoParenthesesOneArgument();

  @Nullable
  String functionName();

  @Nullable
  ASTNode functionNameNode();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  String getName();

  @NotNull
  PsiElement getNameIdentifier();

  @NotNull
  String moduleAttributeName();

  @Nullable
  String moduleName();

  @NotNull
  PsiElement[] primaryArguments();

  @NotNull
  OtpErlangObject quote();

  @Nullable
  String resolvedFunctionName();

  @Nullable
  String resolvedModuleName();

  @Nullable
  PsiElement[] secondaryArguments();

  @NotNull
  PsiElement setName(String newName);

}
