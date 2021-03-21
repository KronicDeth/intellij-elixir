// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMatchedUnqualifiedBracketOperation extends ElixirMatchedExpression, UnqualifiedBracketOperation {

  @NotNull
  ElixirBracketArguments getBracketArguments();

  @NotNull
  ElixirIdentifier getIdentifier();

  @NotNull OtpErlangObject quote();

}
