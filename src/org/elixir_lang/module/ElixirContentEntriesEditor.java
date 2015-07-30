package org.elixir_lang.module;

import com.intellij.openapi.roots.ui.configuration.CommonContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.jps.model.java.JavaSourceRootType;

/**
 * Created by zyuyou on 15/6/5.
 */
public class ElixirContentEntriesEditor extends CommonContentEntriesEditor {

  public ElixirContentEntriesEditor(String moduleName, ModuleConfigurationState state) {
    super(moduleName, state, JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE);
  }
}
