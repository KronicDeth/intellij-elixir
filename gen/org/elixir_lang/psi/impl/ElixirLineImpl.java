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

public class ElixirLineImpl extends ASTWrapperPsiElement implements ElixirLine {

  public ElixirLineImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitLine(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirLineBody getLineBody() {
    return PsiTreeUtil.getChildOfType(this, ElixirLineBody.class);
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
  public @NotNull List<Integer> addEscapedTerminator(@Nullable List<Integer> maybeCodePointList, @NotNull ASTNode child) {
    return ElixirPsiImplUtil.addEscapedTerminator(this, maybeCodePointList, child);
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
  public @Nullable Body getBody() {
    return ElixirPsiImplUtil.getBody(this);
  }

  @Override
  public boolean isCharList() {
    return ElixirPsiImplUtil.isCharList(this);
  }

  @Override
  public @NotNull OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  public @NotNull OtpErlangObject quoteAsAtom() {
    return ElixirPsiImplUtil.quoteAsAtom(this);
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
