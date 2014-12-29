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

public class ElixirMatchedHatOperationImpl extends ASTWrapperPsiElement implements ElixirMatchedHatOperation {

  public ElixirMatchedHatOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedHatOperation(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<ElixirAtCharTokenOrNumberOperation> getAtCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirAtCharTokenOrNumberOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirCaptureCharTokenOrNumberOperation> getCaptureCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCaptureCharTokenOrNumberOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirCharList> getCharListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharList.class);
  }

  @Override
  @NotNull
  public List<ElixirCharListHeredoc> getCharListHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirCharListHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirEmptyParentheses> getEmptyParenthesesList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEmptyParentheses.class);
  }

  @Override
  @NotNull
  public ElixirHatInfixOperator getHatInfixOperator() {
    return findNotNullChildByClass(ElixirHatInfixOperator.class);
  }

  @Override
  @NotNull
  public List<ElixirList> getListList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirList.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedAtOperation> getMatchedAtOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedAtOperation.class);
  }

  @Override
  @Nullable
  public ElixirMatchedHatOperation getMatchedHatOperation() {
    return findChildByClass(ElixirMatchedHatOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedNonNumericCaptureOperation> getMatchedNonNumericCaptureOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedNonNumericCaptureOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirMatchedUnaryOperation> getMatchedUnaryOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMatchedUnaryOperation.class);
  }

  @Override
  @NotNull
  public List<ElixirMaxExpression> getMaxExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirMaxExpression.class);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesCall> getNoParenthesesCallList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesCall.class);
  }

  @Override
  @NotNull
  public List<ElixirNoParenthesesOneExpression> getNoParenthesesOneExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNoParenthesesOneExpression.class);
  }

  @Override
  @NotNull
  public List<ElixirNumber> getNumberList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirNumber.class);
  }

  @Override
  @NotNull
  public List<ElixirSigil> getSigilList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirSigil.class);
  }

  @Override
  @NotNull
  public List<ElixirString> getStringList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirString.class);
  }

  @Override
  @NotNull
  public List<ElixirStringHeredoc> getStringHeredocList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringHeredoc.class);
  }

  @Override
  @NotNull
  public List<ElixirUnaryCharTokenOrNumberOperation> getUnaryCharTokenOrNumberOperationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirUnaryCharTokenOrNumberOperation.class);
  }

}
