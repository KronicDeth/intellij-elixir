package org.elixir_lang.application.options;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeStyleConfigurable extends CodeStyleAbstractConfigurable {
    public CodeStyleConfigurable(@NotNull com.intellij.psi.codeStyle.CodeStyleSettings codeStyleSettings,
                                 @NotNull com.intellij.psi.codeStyle.CodeStyleSettings cloneCodeStyleSettings) {
        super(codeStyleSettings, cloneCodeStyleSettings, "Elixir");
    }

    @Override
    protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new CodeStylePanel(getCurrentSettings(), settings);
    }

    /**
     * Returns the topic in the help file which is shown when help for the configurable
     * is requested.
     *
     * @return the help topic, or null if no help is available.
     */
    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    private static class CodeStylePanel extends TabbedLanguageCodeStylePanel {
        private CodeStylePanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
            super(ElixirLanguage.INSTANCE, currentSettings, settings);
        }
    }
}
