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
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.tree.IElementType;

public class ElixirStringHeredocImpl extends ASTWrapperPsiElement implements ElixirStringHeredoc {

  public ElixirStringHeredocImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStringHeredoc(this);
  }

  @Override
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
  public List<ElixirStringHeredocLine> getStringHeredocLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringHeredocLine.class);
  }

  @Override
  public @NotNull List<Integer> addEscapedCharacterCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child) {
    return ElixirPsiImplUtil.addEscapedCharacterCodePoints(this, codePointList, child);
  }

  @Override
  public @NotNull List<Integer> addEscapedEOL(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child) {
    return ElixirPsiImplUtil.addEscapedEOL(this, maybeCodePointList, child);
  }

  @Override
  public @NotNull List<Integer> addFragmentCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child) {
    return ElixirPsiImplUtil.addFragmentCodePoints(this, codePointList, child);
  }

  @Override
  public @NotNull List<Integer> addHexadecimalEscapeSequenceCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child) {
    return ElixirPsiImplUtil.addHexadecimalEscapeSequenceCodePoints(this, codePointList, child);
  }

  @Override
  public IElementType getFragmentType() {
    return ElixirPsiImplUtil.getFragmentType(this);
  }

  @Override
  public @NotNull List<HeredocLine> getHeredocLineList() {
    return ElixirPsiImplUtil.getHeredocLineList(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  public @NotNull OtpErlangObject quoteBinary(OtpErlangList metadata, List<OtpErlangObject> argumentList) {
    return ElixirPsiImplUtil.quoteBinary(this, metadata, argumentList);
  }

  @Override
  public @NotNull OtpErlangObject quoteEmpty() {
    return ElixirPsiImplUtil.quoteEmpty(this);
  }

  @Override
  public @NotNull OtpErlangObject quoteInterpolation(ElixirInterpolation interpolation) {
    return ElixirPsiImplUtil.quoteInterpolation(this, interpolation);
  }

  @Override
  public @NotNull OtpErlangObject quoteLiteral(List<Integer> codePointList) {
    return ElixirPsiImplUtil.quoteLiteral(this, codePointList);
  }

}
