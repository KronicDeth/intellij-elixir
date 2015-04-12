package org.elixir_lang;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.elixir_lang.mix.ModuleType;

/**
 * @see package org.intellij.erlang.configuration.DefaultModuleEditorsProvider;
 */
public class ModuleConfigurationEditorProvider implements com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider {
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        Module module = state.getRootModel().getModule();
        ModuleConfigurationEditor[] moduleConfigurationEditors = ModuleConfigurationEditor.EMPTY;

        if (com.intellij.openapi.module.ModuleType.get(module) instanceof ModuleType) {
            moduleConfigurationEditors = new ModuleConfigurationEditor[]{
                    new ContentEntriesEditor(module.getName(), state)
            };
        }

        return moduleConfigurationEditors;
    }
}
