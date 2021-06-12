// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.ericsson.otp.erlang.OtpErlangObject;

public interface ElixirAssociationsBase extends Quotable {

  @NotNull
  List<ElixirAlias> getAliasList();

  @NotNull
  List<ElixirAtom> getAtomList();

  @NotNull
  List<ElixirContainerAssociationOperation> getContainerAssociationOperationList();

  @NotNull
  List<ElixirMatchedExpression> getMatchedExpressionList();

  @NotNull
  List<ElixirVariable> getVariableList();

  @NotNull OtpErlangObject quote();

}
