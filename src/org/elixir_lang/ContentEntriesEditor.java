package org.elixir_lang;

import com.intellij.openapi.roots.ui.configuration.CommonContentEntriesEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.jps.model.java.JavaSourceRootType;

public class ContentEntriesEditor extends CommonContentEntriesEditor {
    public ContentEntriesEditor(String moduleName, ModuleConfigurationState state) {
        super(moduleName, state, JavaSourceRootType.SOURCE, JavaSourceRootType.TEST_SOURCE);
    }
}
