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

public class ElixirMatchedNonNumericCaptureOperationImpl extends ASTWrapperPsiElement implements ElixirMatchedNonNumericCaptureOperation {

  public ElixirMatchedNonNumericCaptureOperationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitMatchedNonNumericCaptureOperation(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAlias getAlias() {
    return findChildByClass(ElixirAlias.class);
  }

  @Override
  @Nullable
  public ElixirAtCharTokenOrNumberOperation getAtCharTokenOrNumberOperation() {
    return findChildByClass(ElixirAtCharTokenOrNumberOperation.class);
  }

  @Override
  @Nullable
  public ElixirAtom getAtom() {
    return findChildByClass(ElixirAtom.class);
  }

  @Override
  @Nullable
  public ElixirCaptureCharTokenOrNumberOperation getCaptureCharTokenOrNumberOperation() {
    return findChildByClass(ElixirCaptureCharTokenOrNumberOperation.class);
  }

  @Override
  @NotNull
  public ElixirCapturePrefixOperator getCapturePrefixOperator() {
    return findNotNullChildByClass(ElixirCapturePrefixOperator.class);
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
  @NotNull
  public List<ElixirEscapedCharacter> getEscapedCharacterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirEscapedCharacter.class);
  }

  @Override
  @NotNull
  public List<ElixirHexadecimalEscapeSequence> getHexadecimalEscapeSequenceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirHexadecimalEscapeSequence.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedCharListBody getInterpolatedCharListBody() {
    return findChildByClass(ElixirInterpolatedCharListBody.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedCharListSigilHeredoc getInterpolatedCharListSigilHeredoc() {
    return findChildByClass(ElixirInterpolatedCharListSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedStringBody getInterpolatedStringBody() {
    return findChildByClass(ElixirInterpolatedStringBody.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolation> getInterpolationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolation.class);
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
  public ElixirNoParenthesesManyArgumentsQualifiedCall getNoParenthesesManyArgumentsQualifiedCall() {
    return findChildByClass(ElixirNoParenthesesManyArgumentsQualifiedCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesManyArgumentsUnqualifiedCall getNoParenthesesManyArgumentsUnqualifiedCall() {
    return findChildByClass(ElixirNoParenthesesManyArgumentsUnqualifiedCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesNoArgumentsQualifiedCall getNoParenthesesNoArgumentsQualifiedCall() {
    return findChildByClass(ElixirNoParenthesesNoArgumentsQualifiedCall.class);
  }

  @Override
  @Nullable
  public ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable getNoParenthesesNoArgumentsUnqualifiedCallOrVariable() {
    return findChildByClass(ElixirNoParenthesesNoArgumentsUnqualifiedCallOrVariable.class);
  }

  @Override
  @Nullable
  public ElixirNumber getNumber() {
    return findChildByClass(ElixirNumber.class);
  }

  @Override
  @Nullable
  public ElixirQualifiedAlias getQualifiedAlias() {
    return findChildByClass(ElixirQualifiedAlias.class);
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
