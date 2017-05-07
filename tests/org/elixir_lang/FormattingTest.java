package org.elixir_lang;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.code_style.CodeStyleSettings;

public class FormattingTest extends LightCodeInsightFixtureTestCase {
    private com.intellij.psi.codeStyle.CodeStyleSettings temporaryCodeStyleSettings;

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/formatting";
    }

    private void restoreStyleSettings() {
        CodeStyleSettingsManager.getInstance(getProject()).dropTemporarySettings();
    }

    private void setTestStyleSettings() {
        CodeStyleSettingsManager settingsManager = CodeStyleSettingsManager.getInstance(getProject());
        com.intellij.psi.codeStyle.CodeStyleSettings codeStyleSettings = settingsManager.getCurrentSettings();
        assertNotNull(codeStyleSettings);
        temporaryCodeStyleSettings = codeStyleSettings.clone();
        com.intellij.psi.codeStyle.CodeStyleSettings.IndentOptions indentOptions =
                temporaryCodeStyleSettings.getIndentOptions(ElixirFileType.INSTANCE);
        assertNotNull(indentOptions);
        settingsManager.setTemporarySettings(temporaryCodeStyleSettings);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setTestStyleSettings();
    }

    @Override
    public void tearDown() throws Exception {
        restoreStyleSettings();
        super.tearDown();
    }

    public void testIndentWithoutOverrides() {
        myFixture.configureByFile("indent_without_override_before.ex");

        reformatFixture();

        myFixture.checkResultByFile("indent_without_override_after.ex");
    }

    public void testWithSpaceAroundInMatch() {
        myFixture.configureByFile("without_space_around_in_match.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_IN_MATCH_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_in_match.ex");
    }

    public void testWithoutSpaceAroundInMatch() {
        myFixture.configureByFile("with_space_around_in_match.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_IN_MATCH_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_in_match.ex");
    }

    public void testWithSpaceAroundMatch() {
        myFixture.configureByFile("without_space_around_match.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_ASSIGNMENT_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_match.ex");
    }

    public void testWithoutSpaceAroundMatch() {
        myFixture.configureByFile("with_space_around_match.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_ASSIGNMENT_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_match.ex");
    }

    private void reformatFixture() {
        new WriteCommandAction.Simple(getProject()) {
            @Override
            protected void run() throws Throwable {
                CodeStyleManager.getInstance(getProject()).reformatText(myFixture.getFile(),
                        ContainerUtil.newArrayList(myFixture.getFile().getTextRange()));
            }
        }.execute();
    }
}
