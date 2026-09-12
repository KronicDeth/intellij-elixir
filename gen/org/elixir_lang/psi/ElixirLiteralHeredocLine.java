// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.util.concurrency.annotations.RequiresReadLock;

public interface ElixirLiteralHeredocLine extends HeredocLineable {

  @Nullable
  ElixirEscapedEOL getEscapedEOL();

  @NotNull
  ElixirHeredocLinePrefix getHeredocLinePrefix();

  @NotNull
  ElixirLiteralHeredocLineBody getLiteralHeredocLineBody();

  Body getBody();

  @RequiresReadLock
  @NotNull OtpErlangObject quote(@NotNull HeredocLiteral heredocLiteral, int prefixLength);

}
