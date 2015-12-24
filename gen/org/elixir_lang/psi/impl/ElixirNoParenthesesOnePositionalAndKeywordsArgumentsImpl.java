// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.elixir_lang.psi.ElixirNoParenthesesFirstPositional;
import org.elixir_lang.psi.ElixirNoParenthesesKeywords;
import org.elixir_lang.psi.ElixirNoParenthesesOnePositionalAndKeywordsArguments;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;

public class ElixirNoParenthesesOnePositionalAndKeywordsArgumentsImpl extends ASTWrapperPsiElement implements ElixirNoParenthesesOnePositionalAndKeywordsArguments {

  public ElixirNoParenthesesOnePositionalAndKeywordsArgumentsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitNoParenthesesOnePositionalAndKeywordsArguments(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesFirstPositional getNoParenthesesFirstPositional() {
    return findNotNullChildByClass(ElixirNoParenthesesFirstPositional.class);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesKeywords getNoParenthesesKeywords() {
    return findNotNullChildByClass(ElixirNoParenthesesKeywords.class);
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
