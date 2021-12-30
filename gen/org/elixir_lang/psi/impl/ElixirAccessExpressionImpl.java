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

public class ElixirAccessExpressionImpl extends ASTWrapperPsiElement implements ElixirAccessExpression {

  public ElixirAccessExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitAccessExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirAlias getAlias() {
    return PsiTreeUtil.getChildOfType(this, ElixirAlias.class);
  }

  @Override
  @Nullable
  public ElixirAnonymousFunction getAnonymousFunction() {
    return PsiTreeUtil.getChildOfType(this, ElixirAnonymousFunction.class);
  }

  @Override
  @Nullable
  public ElixirAtom getAtom() {
    return PsiTreeUtil.getChildOfType(this, ElixirAtom.class);
  }

  @Override
  @Nullable
  public ElixirAtomKeyword getAtomKeyword() {
    return PsiTreeUtil.getChildOfType(this, ElixirAtomKeyword.class);
  }

  @Override
  @Nullable
  public ElixirBinaryWholeNumber getBinaryWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirBinaryWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirBitString getBitString() {
    return PsiTreeUtil.getChildOfType(this, ElixirBitString.class);
  }

  @Override
  @Nullable
  public ElixirCaptureNumericOperation getCaptureNumericOperation() {
    return PsiTreeUtil.getChildOfType(this, ElixirCaptureNumericOperation.class);
  }

  @Override
  @Nullable
  public ElixirCharToken getCharToken() {
    return PsiTreeUtil.getChildOfType(this, ElixirCharToken.class);
  }

  @Override
  @Nullable
  public ElixirDecimalFloat getDecimalFloat() {
    return PsiTreeUtil.getChildOfType(this, ElixirDecimalFloat.class);
  }

  @Override
  @Nullable
  public ElixirDecimalWholeNumber getDecimalWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirDecimalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirHeredoc getHeredoc() {
    return PsiTreeUtil.getChildOfType(this, ElixirHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirHexadecimalWholeNumber getHexadecimalWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirHexadecimalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedSigilHeredoc getInterpolatedSigilHeredoc() {
    return PsiTreeUtil.getChildOfType(this, ElixirInterpolatedSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirInterpolatedSigilLine getInterpolatedSigilLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirInterpolatedSigilLine.class);
  }

  @Override
  @Nullable
  public ElixirLine getLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirLine.class);
  }

  @Override
  @Nullable
  public ElixirList getList() {
    return PsiTreeUtil.getChildOfType(this, ElixirList.class);
  }

  @Override
  @Nullable
  public ElixirLiteralSigilHeredoc getLiteralSigilHeredoc() {
    return PsiTreeUtil.getChildOfType(this, ElixirLiteralSigilHeredoc.class);
  }

  @Override
  @Nullable
  public ElixirLiteralSigilLine getLiteralSigilLine() {
    return PsiTreeUtil.getChildOfType(this, ElixirLiteralSigilLine.class);
  }

  @Override
  @Nullable
  public ElixirMapOperation getMapOperation() {
    return PsiTreeUtil.getChildOfType(this, ElixirMapOperation.class);
  }

  @Override
  @Nullable
  public ElixirOctalWholeNumber getOctalWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirOctalWholeNumber.class);
  }

  @Override
  @Nullable
  public ElixirParentheticalStab getParentheticalStab() {
    return PsiTreeUtil.getChildOfType(this, ElixirParentheticalStab.class);
  }

  @Override
  @Nullable
  public ElixirStructOperation getStructOperation() {
    return PsiTreeUtil.getChildOfType(this, ElixirStructOperation.class);
  }

  @Override
  @Nullable
  public ElixirTuple getTuple() {
    return PsiTreeUtil.getChildOfType(this, ElixirTuple.class);
  }

  @Override
  @Nullable
  public ElixirUnknownBaseWholeNumber getUnknownBaseWholeNumber() {
    return PsiTreeUtil.getChildOfType(this, ElixirUnknownBaseWholeNumber.class);
  }

  @Override
  public boolean isModuleName() {
    return ElixirPsiImplUtil.isModuleName(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

}
