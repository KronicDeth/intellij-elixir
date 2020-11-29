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
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.psi.tree.IElementType;

public class ElixirInterpolatedSigilHeredocImpl extends ASTWrapperPsiElement implements ElixirInterpolatedSigilHeredoc {

  public ElixirInterpolatedSigilHeredocImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitInterpolatedSigilHeredoc(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirHeredocPrefix getHeredocPrefix() {
    return PsiTreeUtil.getChildOfType(this, ElixirHeredocPrefix.class);
  }

  @Override
  @NotNull
  public List<ElixirInterpolatedSigilHeredocLine> getInterpolatedSigilHeredocLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirInterpolatedSigilHeredocLine.class);
  }

  @Override
  @Nullable
  public ElixirSigilModifiers getSigilModifiers() {
    return PsiTreeUtil.getChildOfType(this, ElixirSigilModifiers.class);
  }

  @Override
  @NotNull
  public List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode node) {
    return ElixirPsiImplUtil.addEscapedCharacterCodePoints(this, codePointList, node);
  }

  @Override
  @NotNull
  public List<Integer> addEscapedEOL(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode node) {
    return ElixirPsiImplUtil.addEscapedEOL(this, maybeCodePointList, node);
  }

  @Override
  @NotNull
  public List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode node) {
    return ElixirPsiImplUtil.addFragmentCodePoints(this, codePointList, node);
  }

  @Override
  @NotNull
  public List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode node) {
    return ElixirPsiImplUtil.addHexadecimalEscapeSequenceCodePoints(this, codePointList, node);
  }

  @Override
  public IElementType getFragmentType() {
    return ElixirPsiImplUtil.getFragmentType(this);
  }

  @Override
  @NotNull
  public List<HeredocLine> getHeredocLineList() {
    return ElixirPsiImplUtil.getHeredocLineList(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote(@NotNull OtpErlangObject quotedContent) {
    return ElixirPsiImplUtil.quote(this, quotedContent);
  }

  @Override
  @NotNull
  public OtpErlangObject quoteBinary(OtpErlangTuple binary) {
    return ElixirPsiImplUtil.quoteBinary(this, binary);
  }

  @Override
  @NotNull
  public OtpErlangObject quoteEmpty() {
    return ElixirPsiImplUtil.quoteEmpty(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quoteLiteral(List<Integer> codePointList) {
    return ElixirPsiImplUtil.quoteLiteral(this, codePointList);
  }

  @Override
  public String sigilDelimiter() {
    return ElixirPsiImplUtil.sigilDelimiter(this);
  }

  @Override
  public char sigilName() {
    return ElixirPsiImplUtil.sigilName(this);
  }

}
