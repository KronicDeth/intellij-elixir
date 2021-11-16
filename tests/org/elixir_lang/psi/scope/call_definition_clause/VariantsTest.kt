package org.elixir_lang.psi.scope.call_definition_clause;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;

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

    public void testIssue462() {
        myFixture.configureByFiles("self_completion.ex");
        PsiElement head = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset() - 1)
                .getParent()
                .getParent();
        assertInstanceOf(head, Call.class);
        PsiReference reference = head.getReference();
        assertNotNull("Call definition head does not have a reference", reference);
        Object[] variants = reference.getVariants();
        int count = 0;

        for (Object variant : variants) {
            if (variant instanceof LookupElement) {
                LookupElement lookupElement = (LookupElement) variant;

                if (lookupElement.getLookupString().equals("the_function_currently_being_defined")) {
                      count += 1;
                }
            }
        }

        assertEquals("There is at least one entry for the function currently being defined in variants", 0, count);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/psi/scope/call_definition_clause/variants";
    }

}
