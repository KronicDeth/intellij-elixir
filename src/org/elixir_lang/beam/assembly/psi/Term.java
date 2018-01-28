// This is a generated file. Not intended for manual editing.
package org.elixir_lang.beam.assembly.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface Term extends PsiElement {

  @Nullable
  FunctionReference getFunctionReference();

  @Nullable
  List getList();

  @Nullable
  Map getMap();

  @Nullable
  Struct getStruct();

  @Nullable
  Tuple getTuple();

  @Nullable
  Values getValues();

}
