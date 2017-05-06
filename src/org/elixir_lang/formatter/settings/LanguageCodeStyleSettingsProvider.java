package org.elixir_lang.formatter.settings;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
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

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            //consumer.showAllStandardOptions();
            consumer.showStandardOptions(
                    // SPACE_BEFORE_PARENTHESES group

                    /* SPACE_BEFORE_METHOD_PARENTHESES - Disabled because space between function name and call arguments
                       is invalid for parenthesized arguments */
                    /* SPACE_BEFORE_METHOD_CALL_PARENTHESES - Disabled because space between function name and call
                       arguments is invalid for parenthesized arguments */
                    /* SPACE_BEFORE_IF_PARENTHESES - Disabled because `if` is not special, so no specific setting for it
                       since it's not a keyword unlike in Java */
                    /* SPACE_BEFORE_FOR_PARENTHESES - Disabled because `for` is a special form, but you wouldn't put
                       parentheses around the `<-` clauses */
                    /* SPACE_BEFORE_WHILE_PARENTHESES - There is no `while` in Elixir */
                    /* SPACE_BEFORE_SWITCH_PARENTHESES - `switch` in Java would be `case` in Elixir and no style
                       recommends parentheses for it */
                    /* SPACE_BEFORE_CATCH_PARENTHESES - `catch` in Java would be `rescue` in Elixir, but `rescue` uses
                       `->` clauses, so its rules would apply */
                    /* SPACE_BEFORE_SYNCHRONIZED_PARENTHESES - no `synchronized` in Elixir */
                    /* SPACE_BEFORE_ANNOTATION_PARAMETER_LIST - module attribute calls follow normal call rules and a
                       space isn't allowed between the function name and parenthesized arguments */

                    // SPACE_AROUND_OPERATORS group
                    "SPACE_AROUND_ASSIGNMENT_OPERATORS"
            );

            consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Match operator (=)");
        }
    }

    @NotNull
    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        String codeSample = null;

        switch (settingsType) {
            case BLANK_LINES_SETTINGS:
                codeSample = "Blank Line Settings Code Sample";
                break;
            case SPACING_SETTINGS:
                codeSample = "a = 1";
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
