package org.elixir_lang.psi.scope.call_definition_clause;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.util.List;

public class VariantsTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testIssue453() {
        myFixture.configureByFiles("defmodule.ex");
        myFixture.complete(CompletionType.BASIC);
        List<String> strings = myFixture.getLookupElementStrings();
        assertNotNull("Completion not shown", strings);
        assertEquals("Wrong number of completions", 0, strings.size());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/psi/scope/call_definition_clause/variants";
    }

}
