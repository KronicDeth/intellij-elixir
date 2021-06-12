// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirMapUpdateArguments extends Quotable {

  @Nullable
  ElixirAssociations getAssociations();

  @Nullable
  ElixirAssociationsBase getAssociationsBase();

  @Nullable
  ElixirKeywords getKeywords();

  @NotNull
  ElixirMatchedMatchOperation getMatchedMatchOperation();

  @NotNull
  ElixirPipeInfixOperator getPipeInfixOperator();

  @NotNull OtpErlangObject quote();

}
