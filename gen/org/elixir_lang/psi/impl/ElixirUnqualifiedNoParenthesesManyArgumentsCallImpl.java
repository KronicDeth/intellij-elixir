// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl extends ASTWrapperPsiElement implements ElixirUnqualifiedNoParenthesesManyArgumentsCall {

  public ElixirUnqualifiedNoParenthesesManyArgumentsCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitUnqualifiedNoParenthesesManyArgumentsCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArguments getNoParenthesesManyArguments() {
    return findChildByClass(ElixirNoParenthesesManyArguments.class);
  }

  @Override
  @NotNull
  public ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier getNoParenthesesManyArgumentsUnqualifiedIdentifier() {
    return findNotNullChildByClass(ElixirNoParenthesesManyArgumentsUnqualifiedIdentifier.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesStrict getNoParenthesesStrict() {
    return findChildByClass(ElixirNoParenthesesStrict.class);
  }

  @NotNull
  public QuotableArguments getArguments() {
    return ElixirPsiImplUtil.getArguments(this);
  }

  @NotNull
  public Quotable getIdentifier() {
    return ElixirPsiImplUtil.getIdentifier(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public OtpErlangObject[] quoteArguments() {
    return ElixirPsiImplUtil.quoteArguments(this);
  }

  public OtpErlangObject quoteIdentifier() {
    return ElixirPsiImplUtil.quoteIdentifier(this);
  }

}
