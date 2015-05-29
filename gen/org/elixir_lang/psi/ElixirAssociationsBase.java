// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ElixirAssociationsBase extends Quotable {

  @NotNull
  List<ElixirContainerAssociationOperation> getContainerAssociationOperationList();

  @NotNull
  OtpErlangObject quote();

}
