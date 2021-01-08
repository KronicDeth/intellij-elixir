package org.elixir_lang.folding;

import com.intellij.application.options.editor.CodeFoldingOptionsProvider;
import com.intellij.openapi.options.BeanConfigurable;

public class OptionsProvider extends BeanConfigurable<ElixirFoldingSettings> implements CodeFoldingOptionsProvider {
    public OptionsProvider() {
        super(ElixirFoldingSettings.getInstance());
        ElixirFoldingSettings settings = ElixirFoldingSettings.getInstance();
        checkBox("Elixir module attributes", settings::isReplaceModuleAttributesWithValues, value -> settings.getState().REPLACE_MODULE_ATTRIBUTES_WITH_VALUES = value);
        checkBox("Elixir Module directive (`alias`, `import`, `require` or `use`) groups", settings::isCollapseElixirModuleDirectiveGroups, value -> settings.getState().COLLAPSE_ELIXIR_MODULE_DIRECTIVE_GROUPS = value);
    }
}
