package org.elixir_lang.formatter.settings;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NotNull;

public class LanguageCodeStyleSettingsProvider extends com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider {
    private static final String INDENT_CODE_SAMPLE =
            "defmodule Foo do\n" +
            "  def foo do\n" +
            "    receive do\n" +
            "      {:ok, value} -> value\n" +
            "      {:error, reason} -> reason\n" +
            "    end\n" +
            "  end\n" +
            "end";

    @NotNull
    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        String codeSample = null;

        switch (settingsType) {
            case BLANK_LINES_SETTINGS:
                codeSample = "Blank Line Settings Code Sample";
                break;
            case SPACING_SETTINGS:
                codeSample = "Spacing Settings Code Sample";
                break;
            case WRAPPING_AND_BRACES_SETTINGS:
                codeSample = "Wrapping And Braces Settings Code Sample";
                break;
            case INDENT_SETTINGS:
                codeSample = INDENT_CODE_SAMPLE;
                break;
            case LANGUAGE_SPECIFIC:
                codeSample = "Language Specific Code Sample";
                break;
        }

        return codeSample;
    }

    @Override
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings defaultCommonSettings = new CommonCodeStyleSettings(getLanguage());

        CommonCodeStyleSettings.IndentOptions indentOptions = defaultCommonSettings.initIndentOptions();
        indentOptions.INDENT_SIZE = 2;
        indentOptions.CONTINUATION_INDENT_SIZE = 4;
        indentOptions.TAB_SIZE = 2;

        return defaultCommonSettings;
    }

    @NotNull
    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return ElixirLanguage.INSTANCE;
    }
}
