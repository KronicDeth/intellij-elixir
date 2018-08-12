// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ElixirKeywordKey extends NamedElement, Quotable {

  @Nullable
  ElixirCharListLine getCharListLine();

  @Nullable
  ElixirStringLine getStringLine();

  @Nullable
  String getName();

  @Nullable
  PsiElement getNameIdentifier();

  @NotNull
  OtpErlangObject quote();

  @NotNull
  PsiElement setName(@NotNull String newName);

}
