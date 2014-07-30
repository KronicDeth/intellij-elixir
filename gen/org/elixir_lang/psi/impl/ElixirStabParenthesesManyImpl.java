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

public class ElixirStabParenthesesManyImpl extends ASTWrapperPsiElement implements ElixirStabParenthesesMany {

  public ElixirStabParenthesesManyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStabParenthesesMany(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirCallArgumentsNoParenthesesKeyword getCallArgumentsNoParenthesesKeyword() {
    return findChildByClass(ElixirCallArgumentsNoParenthesesKeyword.class);
  }

  @Override
  @Nullable
  public ElixirCallArgumentsNoParenthesesMany getCallArgumentsNoParenthesesMany() {
    return findChildByClass(ElixirCallArgumentsNoParenthesesMany.class);
  }

  @Override
  @Nullable
  public ElixirCloseParenthesis getCloseParenthesis() {
    return findChildByClass(ElixirCloseParenthesis.class);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @Nullable
  public ElixirOpenParenthesis getOpenParenthesis() {
    return findChildByClass(ElixirOpenParenthesis.class);
  }

}
