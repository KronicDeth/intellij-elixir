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

public class ElixirCallArgumentsNoParenthesesKeywordsExpressionImpl extends ASTWrapperPsiElement implements ElixirCallArgumentsNoParenthesesKeywordsExpression {

  public ElixirCallArgumentsNoParenthesesKeywordsExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitCallArgumentsNoParenthesesKeywordsExpression(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation() {
    return findChildByClass(ElixirAtCharTokenOrNumberOperation.class);
  }

  @Override
  @Nullable
  public ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation() {
    return findChildByClass(ElixirCaptureCharTokenOrNumberOperation.class);
  }

  @Override
  @Nullable
  public ElixirCharList getCharList() {
    return findChildByClass(ElixirCharList.class);
  }

  @Override
  @Nullable
  public ElixirCharListHeredoc getCharListHeredoc() {
    return findChildByClass(ElixirCharListHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirEmptyParentheses getEmptyParentheses() {
    return findChildByClass(ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirKeywordKey getKeywordKey() {
    return findNotNullChildByClass(ElixirKeywordKey.class);
  }

  @Override
  @Nullable
  public ElixirList getList() {
    return findChildByClass(ElixirList.class);
  }

  @Override
  @Nullable
  public ElixirMatchedAtOperation getMatchedAtOperation() {
    return findChildByClass(ElixirMatchedAtOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedHatOperation getMatchedHatOperation() {
    return findChildByClass(ElixirMatchedHatOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedMultiplicationOperation getMatchedMultiplicationOperation() {
    return findChildByClass(ElixirMatchedMultiplicationOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedNonNumericCaptureOperation getMatchedNonNumericCaptureOperation() {
    return findChildByClass(ElixirMatchedNonNumericCaptureOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedUnaryOperation getMatchedUnaryOperation() {
    return findChildByClass(ElixirMatchedUnaryOperation.class);
  }

  @Override
  @Nullable
  public ElixirMaxExpression getMaxExpression() {
    return findChildByClass(ElixirMaxExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesCall getNoParenthesesCall() {
    return findChildByClass(ElixirNoParenthesesCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyStrictNoParenthesesExpression getNoParenthesesManyStrictNoParenthesesExpression() {
    return findChildByClass(ElixirNoParenthesesManyStrictNoParenthesesExpression.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesOneExpression getNoParenthesesOneExpression() {
    return findChildByClass(ElixirNoParenthesesOneExpression.class);
  }

  @Override
  @Nullable
  public ElixirNumber getNumber() {
    return findChildByClass(ElixirNumber.class);
  }

  @Override
  @Nullable
  public ElixirSigil getSigil() {
    return findChildByClass(ElixirSigil.class);
  }

  @Override
  @Nullable
  public ElixirString getString() {
    return findChildByClass(ElixirString.class);
  }

  @Override
  @Nullable
  public ElixirStringHeredoc getStringHeredoc() {
    return findChildByClass(ElixirStringHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirUnaryCharTokenOrNumberOperation getUnaryCharTokenOrNumberOperation() {
    return findChildByClass(ElixirUnaryCharTokenOrNumberOperation.class);
  }

}
