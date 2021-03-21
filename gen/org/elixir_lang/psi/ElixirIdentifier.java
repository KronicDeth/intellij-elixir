// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;

public interface ElixirIdentifier extends Quotable {

  @Nullable ItemPresentation getPresentation();

  @Nullable PsiReference getReference();

  @NotNull OtpErlangObject quote();

}
