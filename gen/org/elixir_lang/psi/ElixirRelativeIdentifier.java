// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirRelativeIdentifier extends Quotable {

  @Nullable
  ElixirAtomKeyword getAtomKeyword();

  @Nullable
  ElixirLine getLine();

  @RequiresReadLock
  @NotNull OtpErlangObject quote();

}
