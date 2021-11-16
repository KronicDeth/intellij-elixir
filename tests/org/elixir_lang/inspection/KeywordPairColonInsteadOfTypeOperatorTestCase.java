package org.elixir_lang.inspection;

import org.elixir_lang.PlatformTestCase;

public class KeywordPairColonInsteadOfTypeOperatorTestCase extends PlatformTestCase {
    public void testIssue525() {
        myFixture.configureByFiles("issue_525.ex");
        myFixture.enableInspections(KeywordPairColonInsteadOfTypeOperator.class);
        myFixture.checkHighlighting();
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/inspection/keyword_pair_colon_instead_of_type_operator_test_case";
    }
}
