// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiReference;

public interface ElixirAtom extends NavigatablePsiElement, PsiNamedElement, Quotable {

  @Nullable
  ElixirCharListLine getCharListLine();

  @Nullable
  ElixirStringLine getStringLine();

  @Nullable PsiReference getReference();

  @NotNull OtpErlangObject quote();

}
