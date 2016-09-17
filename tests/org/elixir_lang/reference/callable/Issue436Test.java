package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall;

import static org.elixir_lang.reference.Callable.isParameter;
import static org.elixir_lang.reference.Callable.isVariable;

public class Issue436Test extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testIsParameter() {
        myFixture.configureByFiles("is_parameter.ex");
        PsiElement variable = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling()
                .getLastChild()
                .getLastChild();

        assertInstanceOf(variable, UnqualifiedNoArgumentsCall.class);
        assertFalse("alias is marked as a parameter", isParameter(variable));
        assertTrue("alias is not marked as a variable", isVariable(variable));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_436";
    }
}
