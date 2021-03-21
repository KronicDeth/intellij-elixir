// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirStructOperation extends Quotable {

  @Nullable
  ElixirAlias getAlias();

  @Nullable
  ElixirAtom getAtom();

  @NotNull
  ElixirMapArguments getMapArguments();

  @NotNull
  ElixirMapPrefixOperator getMapPrefixOperator();

  @Nullable
  ElixirMatchedExpression getMatchedExpression();

  @Nullable
  ElixirVariable getVariable();

  @NotNull OtpErlangObject quote();

}
