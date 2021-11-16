package org.elixir_lang.inspection;

import org.elixir_lang.PlatformTestCase;

public class MatchOperatorInsteadOfTypeOperatorTestCase extends PlatformTestCase {
    public void testMatchOperator() {
        myFixture.configureByFiles("match_operator.ex");
        myFixture.enableInspections(MatchOperatorInsteadOfTypeOperator.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/inspection/match_operator_instead_of_type_operator_test_case";
    }
}
