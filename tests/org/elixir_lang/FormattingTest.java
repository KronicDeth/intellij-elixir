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

    public void testAnonymousFunctionWithMultipleClauses() {
        String path = "anonymous_function_with_multiple_clauses.ex";

        myFixture.configureByFile(path);

        reformatFixture();

        myFixture.checkResultByFile(path);
    }

    public void testAnonymousFunctionWithMultipleClausesChop() {
        myFixture.configureByFile("anonymous_function_with_multiple_clauses_chop_before.ex");

        reformatFixture();

        myFixture.checkResultByFile("anonymous_function_with_multiple_clauses_chop_after.ex");
    }

    // TODO fix indentation of body after wrapping: it is relative to stab operation start instead start of line
    public void testAnonymousFunctionWithSingleClause() {
        String path = "anonymous_function_with_single_clause.ex";

        myFixture.configureByFiles(path);

        reformatFixture();

        myFixture.checkResultByFile(path);
    }

    public void testNoSpaceAfterAtOperator() {
        myFixture.configureByFile("space_after_at_operator.ex");

        reformatFixture();

        myFixture.checkResultByFile("no_space_after_at_operator.ex");
    }

    public void testCaptureAndSymbolOperatorsWithoutSpaceAfterCapture() {
        String path = "capture_and_symbol_operators_without_space_after_capture.ex";

        myFixture.configureByFile(path);

        temporaryCodeStyleSettings
                .getCustomSettings(CodeStyleSettings.class)
                .SPACE_AFTER_CAPTURE_OPERATOR = false;
        temporaryCodeStyleSettings
                .getCommonSettings(ElixirLanguage.INSTANCE)
                .SPACE_AROUND_MULTIPLICATIVE_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile(path);
    }

    public void testCaptureNameArityWithSpaceAroundMultiplication() {
        String path = "capture_name_arity_with_space_around_multiplication.ex";

        myFixture.configureByFile(path);

        temporaryCodeStyleSettings
                .getCommonSettings(ElixirLanguage.INSTANCE)
                .SPACE_AROUND_MULTIPLICATIVE_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile(path);
    }

    public void testCaptureQualifierDotNameArityWithSpaceAroundMultiplication() {
        String path = "capture_qualifier_dot_name_arity_with_space_around_multiplication.ex";

        myFixture.configureByFile(path);

        temporaryCodeStyleSettings
                .getCommonSettings(ElixirLanguage.INSTANCE)
                .SPACE_AROUND_MULTIPLICATIVE_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile(path);
    }

    public void testIndentWithoutOverrides() {
        myFixture.configureByFile("indent_without_override_before.ex");

        reformatFixture();

        myFixture.checkResultByFile("indent_without_override_after.ex");
    }

    public void testWhenWrapsWithRightOperand() {
        myFixture.configureByFile("when_wraps_with_right_operand_before.ex");

        reformatFixture();

        myFixture.checkResultByFile("when_wraps_with_right_operand_after.ex");
    }

    public void testWithSpaceAroundAdditionOperators() {
        myFixture.configureByFile("without_space_around_addition_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_ADDITIVE_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_addition_operators.ex");
    }

    public void testWithoutSpaceAroundAdditionOperators() {
        myFixture.configureByFile("with_space_around_addition_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_ADDITIVE_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_addition_operators.ex");
    }

    public void testCaptureRightOperandWithoutSpaceAroundAndOperators() {
        myFixture.configureByFile("capture_right_operand_without_space_around_and_operators_before.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_AND_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("capture_right_operand_without_space_around_and_operators_after.ex");
    }

    public void testWithSpaceAroundAndOperators() {
        myFixture.configureByFile("without_space_around_and_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_AND_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_and_operators.ex");
    }

    public void testWithoutSpaceAroundAndOperators() {
        myFixture.configureByFile("with_space_around_and_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_AND_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_and_operators.ex");
    }

    public void testWithSpaceAroundArrowOperators() {
        myFixture.configureByFile("without_space_around_arrow_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_ARROW_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_arrow_operators.ex");
    }

    public void testWithoutSpaceAroundArrowOperators() {
        myFixture.configureByFile("with_space_around_arrow_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_ARROW_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_arrow_operators.ex");
    }

    public void testWithSpaceAfterCaptureOperator() {
        myFixture.configureByFile("without_space_after_capture_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AFTER_CAPTURE_OPERATOR = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_after_capture_operator.ex");
    }

    public void testWithoutSpaceAfterCaptureOperator() {
        myFixture.configureByFile("with_space_after_capture_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AFTER_CAPTURE_OPERATOR = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_after_capture_operator.ex");
    }

    public void testWithSpaceWithinBits() {
        myFixture.configureByFile("without_space_within_bits.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_WITHIN_BITS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_within_bits.ex");
    }

    public void testWithoutSpaceWithinBits() {
        myFixture.configureByFile("with_space_within_bits.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_WITHIN_BITS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_within_bits.ex");
    }

    public void testWithSpaceWithinBrackets() {
        myFixture.configureByFile("without_space_within_brackets.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_WITHIN_BRACKETS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_within_brackets.ex");
    }

    public void testWithoutSpaceWithinBrackets() {
        myFixture.configureByFile("with_space_within_brackets.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_WITHIN_BRACKETS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_within_brackets.ex");
    }

    public void testWithSpaceAfterComma() {
        myFixture.configureByFile("without_space_after_comma.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AFTER_COMMA = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_after_comma.ex");
    }

    public void testWithoutSpaceAfterComma() {
        myFixture.configureByFile("with_space_after_comma.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AFTER_COMMA = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_after_comma.ex");
    }

    public void testWithSpaceBeforeComma() {
        myFixture.configureByFile("without_space_before_comma.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_BEFORE_COMMA = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_before_comma.ex");
    }

    public void testWithoutSpaceBeforeComma() {
        myFixture.configureByFile("with_space_before_comma.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_BEFORE_COMMA = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_before_comma.ex");
    }

    public void testWithSpaceAroundComparisonOperators() {
        myFixture.configureByFile("without_space_around_comparison_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_EQUALITY_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_comparison_operators.ex");
    }

    public void testWithoutSpaceAroundComparisonOperators() {
        myFixture.configureByFile("with_space_around_comparison_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_EQUALITY_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_comparison_operators.ex");
    }

    public void testWithSpaceWithinCurlyBraces() {
        myFixture.configureByFile("without_space_within_curly_braces.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_WITHIN_BRACES = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_within_curly_braces.ex");
    }

    public void testWithoutSpaceWithinCurlyBraces() {
        myFixture.configureByFile("with_space_within_curly_braces.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_WITHIN_BRACES = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_within_curly_braces.ex");
    }

    public void testNoSpaceAroundDotOperator() {
        myFixture.configureByFile("incorrect_spaces_around_dot_operator.ex");

        reformatFixture();

        myFixture.checkResultByFile("no_spaces_around_dot_operator.ex");
    }

    public void testWithSpaceAroundInMatchOperators() {
        myFixture.configureByFile("without_space_around_in_match_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_IN_MATCH_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_in_match_operators.ex");
    }

    public void testWithoutSpaceAroundInMatchOperators() {
        myFixture.configureByFile("with_space_around_in_match_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_IN_MATCH_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_in_match_operators.ex");
    }

    public void testSpaceAroundInOperator() {
        myFixture.configureByFile("incorrect_spaces_around_in_operator.ex");

        reformatFixture();

        myFixture.checkResultByFile("space_around_in_operator.ex");
    }

    public void testWithSpaceAroundMatchOperator() {
        myFixture.configureByFile("without_space_around_match_operator.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_ASSIGNMENT_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_match_operator.ex");
    }

    public void testWithoutSpaceAroundMatchOperator() {
        myFixture.configureByFile("with_space_around_match_operator.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_ASSIGNMENT_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_match_operator.ex");
    }

    public void testWithSpaceAroundMultiplicationOperators() {
        myFixture.configureByFile("without_space_around_multiplication_operators.ex");

        temporaryCodeStyleSettings
                .getCommonSettings(ElixirLanguage.INSTANCE)
                .SPACE_AROUND_MULTIPLICATIVE_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_multiplication_operators.ex");
    }

    public void testWithoutSpaceAroundMultiplicationOperators() {
        myFixture.configureByFile("with_space_around_multiplication_operators.ex");

        temporaryCodeStyleSettings
                .getCommonSettings(ElixirLanguage.INSTANCE)
                .SPACE_AROUND_MULTIPLICATIVE_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_multiplication_operators.ex");
    }

    public void testWithSpaceAroundOrOperators() {
        myFixture.configureByFile("without_space_around_or_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_OR_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_or_operators.ex");
    }

    public void testWithoutSpaceAroundOrOperators() {
        myFixture.configureByFile("with_space_around_or_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_OR_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_or_operators.ex");
    }

    public void testWithSpaceWithinParentheses() {
        myFixture.configureByFile("without_space_within_parentheses.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_WITHIN_PARENTHESES = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_within_parentheses.ex");
    }

    public void testWithoutSpaceWithinParentheses() {
        myFixture.configureByFile("with_space_within_parentheses.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_WITHIN_PARENTHESES = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_within_parentheses.ex");
    }

    public void testWithSpaceAroundPipeOperators() {
        myFixture.configureByFile("without_space_around_pipe_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_PIPE_OPERATOR = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_pipe_operator.ex");
    }

    public void testWithoutSpaceAroundPipeOperators() {
        myFixture.configureByFile("with_space_around_pipe_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_PIPE_OPERATOR = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_pipe_operator.ex");
    }

    public void testWithSpaceAroundRangeOperator() {
        myFixture.configureByFile("without_space_around_range_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_RANGE_OPERATOR = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_range_operator.ex");
    }

    public void testWithoutSpaceAroundRangeOperator() {
        myFixture.configureByFile("with_space_around_range_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_RANGE_OPERATOR = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_range_operator.ex");
    }

    public void testWithSpaceAroundRelationalOperators() {
        myFixture.configureByFile("without_space_around_relational_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_RELATIONAL_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_relational_operators.ex");
    }

    public void testWithoutSpaceAroundRelationalOperators() {
        myFixture.configureByFile("with_space_around_relational_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_RELATIONAL_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_relational_operators.ex");
    }

    public void testWithSpaceAroundStabOperator() {
        myFixture.configureByFile("without_space_around_stab_operator.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_LAMBDA_ARROW = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_stab_operator.ex");
    }

    public void testWithoutSpaceAroundStabOperator() {
        myFixture.configureByFile("with_space_around_stab_operator.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_LAMBDA_ARROW = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_stab_operator.ex");
    }

    public void testWithSpaceAroundThreeOperator() {
        myFixture.configureByFile("without_space_around_three_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_THREE_OPERATOR = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_three_operator.ex");
    }

    public void testWithoutSpaceAroundThreeOperator() {
        myFixture.configureByFile("with_space_around_three_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_THREE_OPERATOR = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_three_operator.ex");
    }

    public void testWithSpaceAroundTwoOperator() {
        myFixture.configureByFile("without_space_around_two_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_TWO_OPERATORS = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_two_operators.ex");
    }

    public void testWithoutSpaceAroundTwoOperator() {
        myFixture.configureByFile("with_space_around_two_operators.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_TWO_OPERATORS = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_two_operators.ex");
    }

    public void testWithSpaceAroundTypeOperator() {
        myFixture.configureByFile("without_space_around_type_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_TYPE_OPERATOR = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_type_operator.ex");
    }

    public void testWithoutSpaceAroundTypeOperator() {
        myFixture.configureByFile("with_space_around_type_operator.ex");

        temporaryCodeStyleSettings.getCustomSettings(CodeStyleSettings.class).SPACE_AROUND_TYPE_OPERATOR = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_type_operator.ex");
    }

    public void testWithSpaceAroundUnaryOperators() {
        myFixture.configureByFile("without_space_around_unary_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_UNARY_OPERATOR = true;

        reformatFixture();

        myFixture.checkResultByFile("with_space_around_unary_operators.ex");
    }

    public void testWithoutSpaceAroundUnaryOperators() {
        myFixture.configureByFile("with_space_around_unary_operators.ex");

        temporaryCodeStyleSettings.getCommonSettings(ElixirLanguage.INSTANCE).SPACE_AROUND_UNARY_OPERATOR = false;

        reformatFixture();

        myFixture.checkResultByFile("without_space_around_unary_operators.ex");
    }

    public void testSpaceAroundWhenOperators() {
        myFixture.configureByFile("incorrect_spaces_around_when_operator.ex");

        reformatFixture();

        myFixture.checkResultByFile("space_around_when_operator.ex");
    }

    public void testSingleSpaceNotOperator() {
        myFixture.configureByFile("multispace_not_operator.ex");

        reformatFixture();

        myFixture.checkResultByFile("single_space_not_operator.ex");
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
