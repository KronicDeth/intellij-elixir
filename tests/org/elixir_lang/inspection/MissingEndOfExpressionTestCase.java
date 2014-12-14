package org.elixir_lang.inspection;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

/**
 * Created by luke.imhoff on 12/6/14.
 */
public class MissingEndOfExpressionTestCase extends LightCodeInsightFixtureTestCase {
    public void testCharTokenNumber() {
        myFixture.configureByFiles("CharTokenNumber.ex");
        myFixture.enableInspections(MissingEndOfExpression.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return System.getenv("INTELLIJ_ELIXIR_PATH") + "/testData/org/elixir_lang/inspection/missing_end_of_expression_test_case";
    }
}
