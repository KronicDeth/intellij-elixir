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

public class ElixirStringLineBodyImpl extends ASTWrapperPsiElement implements ElixirStringLineBody {

  public ElixirStringLineBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStringLineBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
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
  public List<ElixirInterpolation> getInterpolationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolation.class);
  }

  @Override
  @NotNull
  public List<ElixirQuoteHexadecimalEscapeSequence> getQuoteHexadecimalEscapeSequenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirQuoteHexadecimalEscapeSequence.class);
  }

}
