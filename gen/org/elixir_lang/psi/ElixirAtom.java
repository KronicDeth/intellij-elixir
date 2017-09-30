// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirAtom extends NavigatablePsiElement, Quotable {

  @Nullable
  ElixirCharListLine getCharListLine();

  @Nullable
  ElixirStringLine getStringLine();

  @Nullable
  PsiReference getReference();

  @NotNull
  OtpErlangObject quote();

}
