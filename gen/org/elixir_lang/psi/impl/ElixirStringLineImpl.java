// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.psi.Body;
import org.elixir_lang.psi.ElixirQuoteStringBody;
import org.elixir_lang.psi.ElixirStringLine;
import org.elixir_lang.psi.ElixirVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElixirStringLineImpl extends ASTWrapperPsiElement implements ElixirStringLine {

  public ElixirStringLineImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ElixirVisitor visitor) {
    visitor.visitStringLine(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) accept((ElixirVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirQuoteStringBody getQuoteStringBody() {
    return PsiTreeUtil.getChildOfType(this, ElixirQuoteStringBody.class);
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
  public Body getBody() {
    return ElixirPsiImplUtil.getBody(this);
  }

  @Override
  public IElementType getFragmentType() {
    return ElixirPsiImplUtil.getFragmentType(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @Override
  @NotNull
  public OtpErlangObject quoteAsAtom() {
    return ElixirPsiImplUtil.quoteAsAtom(this);
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

}
