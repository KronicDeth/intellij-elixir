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

public class ElixirStringHeredocImpl extends ASTWrapperPsiElement implements ElixirStringHeredoc {

  public ElixirStringHeredocImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitStringHeredoc(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public ElixirHeredocPrefix getHeredocPrefix() {
    return findChildByClass(ElixirHeredocPrefix.class);
  }

  @Override
  @NotNull
  public List<ElixirStringHeredocLine> getStringHeredocLineList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ElixirStringHeredocLine.class);
  }

  public IElementType getFragmentType() {
    return ElixirPsiImplUtil.getFragmentType(this);
  }

  public List<HeredocLine> getHeredocLineList() {
    return ElixirPsiImplUtil.getHeredocLineList(this);
  }

  @NotNull
  public OtpErlangObject quote() {
    return ElixirPsiImplUtil.quote(this);
  }

  @NotNull
  public OtpErlangObject quoteBinary(OtpErlangTuple binary) {
    return ElixirPsiImplUtil.quoteBinary(this, binary);
  }

  @NotNull
  public OtpErlangObject quoteEmpty() {
    return ElixirPsiImplUtil.quoteEmpty(this);
  }

  @NotNull
  public OtpErlangObject quoteLiteral(List<Integer> codePointList) {
    return ElixirPsiImplUtil.quoteLiteral(this, codePointList);
  }

}
