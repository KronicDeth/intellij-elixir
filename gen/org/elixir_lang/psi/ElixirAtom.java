// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiNamedElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiReference;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirAtom extends NavigatablePsiElement, PsiNamedElement, Quotable {

  @Nullable
  ElixirLine getLine();

  @Nullable PsiReference getReference();

  @NotNull OtpErlangObject quote();

  @RequiresReadLock
  @Nullable String getName();

  @NotNull PsiElement setName(@NotNull String newName);

}
