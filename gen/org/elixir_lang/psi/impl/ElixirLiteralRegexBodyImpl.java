// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElixirLiteralRegexBodyImpl extends ASTWrapperPsiElement implements ElixirLiteralRegexBody {

  public ElixirLiteralRegexBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitLiteralRegexBody(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirEscapedCharacter> getEscapedCharacterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEscapedCharacter.class);
  }

  @Override
  @NotNull
  public List<ElixirEscapedEOL> getEscapedEOLList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEscapedEOL.class);
  }

  @Override
  @NotNull
  public List<ElixirHexadecimalEscapePrefix> getHexadecimalEscapePrefixList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirHexadecimalEscapePrefix.class);
  }

  @Override
  @NotNull
  public List<ElixirSigilHexadecimalEscapeSequence> getSigilHexadecimalEscapeSequenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirSigilHexadecimalEscapeSequence.class);
  }

}
