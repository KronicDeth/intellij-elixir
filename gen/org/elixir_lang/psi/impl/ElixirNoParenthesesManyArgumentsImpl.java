// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirNoParenthesesManyArguments;
import org.elixir_lang.psi.ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments;
import org.elixir_lang.psi.ElixirNoParenthesesOnePositionalAndKeywordsArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElixirNoParenthesesManyArgumentsImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesManyArguments {

  public ElixirNoParenthesesManyArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesManyArguments(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments getNoParenthesesManyPositionalAndMaybeKeywordsArguments() {
    return findChildByClass(ElixirNoParenthesesManyPositionalAndMaybeKeywordsArguments.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesOnePositionalAndKeywordsArguments getNoParenthesesOnePositionalAndKeywordsArguments() {
    return findChildByClass(ElixirNoParenthesesOnePositionalAndKeywordsArguments.class);
  }

  @NotNull
  public PsiElement[] arguments() {
    return ElixirPsiImplUtil.arguments(this);
  }

  @NotNull
  public OtpErlangObject[] quoteArguments() {
    return ElixirPsiImplUtil.quoteArguments(this);
  }

}
