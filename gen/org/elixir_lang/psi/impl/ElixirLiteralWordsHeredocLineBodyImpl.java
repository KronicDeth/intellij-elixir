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

public class ElixirLiteralWordsHeredocLineBodyImpl extends ASTWrapperPsiElement implements ElixirLiteralWordsHeredocLineBody {

  public ElixirLiteralWordsHeredocLineBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitLiteralWordsHeredocLineBody(this);
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
  public List<ElixirHexadecimalEscapePrefix> getHexadecimalEscapePrefixList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirHexadecimalEscapePrefix.class);
  }

  @Override
  @NotNull
  public List<ElixirSigilHexadecimalEscapeSequence> getSigilHexadecimalEscapeSequenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirSigilHexadecimalEscapeSequence.class);
  }

}
