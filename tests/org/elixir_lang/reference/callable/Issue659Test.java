package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;

import static org.elixir_lang.reference.Callable.isParameter;
import static org.elixir_lang.reference.Callable.isVariable;

public class Issue659Test extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testIs() {
        myFixture.configureByFiles("is.ex");
        PsiElement callable = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling();

        assertInstanceOf(callable, Call.class);
        assertFalse(
                "unresolvable no argument call in at bracket operation is incorrectly marked as a parameter",
                isParameter(callable)
        );
        assertFalse(
                "unresolvable no argument call in at bracket operation is incorrectly marked as a variable",
                isVariable(callable)
        );
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_659";
    }
}
