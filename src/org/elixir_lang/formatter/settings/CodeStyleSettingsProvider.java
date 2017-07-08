package org.elixir_lang.formatter.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.application.options.CodeStyleConfigurable;
import org.jetbrains.annotations.NotNull;

public class CodeStyleSettingsProvider extends com.intellij.psi.codeStyle.CodeStyleSettingsProvider {
    @NotNull
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new org.elixir_lang.code_style.CodeStyleSettings(settings);
    }

    @NotNull
    @Override
    public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        return new CodeStyleConfigurable(settings, originalSettings);
    }

    /**
     * Returns the name of the configurable page without creating a Configurable instance.
     *
     * @return the display name of the configurable page.
     * @since 9.0
     */
    @NotNull
    @Override
    public String getConfigurableDisplayName() {
        return ElixirLanguage.INSTANCE.getDisplayName();
    }
}
