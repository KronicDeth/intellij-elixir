// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.NoParentheses;
import org.elixir_lang.psi.qualification.Unqualified;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirUnqualifiedNoParenthesesManyArgumentsCall extends PsiElement, Call, NoParentheses, Unqualified, Quotable, QuotableArguments {

  @Nullable
  ElixirNoParenthesesManyArguments getNoParenthesesManyArguments();

  @NotNull
  ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier getNoParenthesesManyArgumentsUnqualifiedIdentifier();

  @Nullable
  ElixirNoParenthesesStrict getNoParenthesesStrict();

  @Nullable
  String functionName();

  @NotNull
  ASTNode functionNameNode();

  @Nullable
  String moduleName();

  @NotNull
  QuotableArguments getArguments();

  @Nullable
  ElixirDoBlock getDoBlock();

  @NotNull
  Quotable getIdentifier();

  @Nullable
  PsiElement[] primaryArguments();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  OtpErlangObject[] quoteArguments();

  OtpErlangObject quoteIdentifier();

  @NotNull
  String resolvedFunctionName();

  @NotNull
  String resolvedModuleName();

  @Nullable
  PsiElement[] secondaryArguments();

}
