package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall;

import static org.elixir_lang.reference.Callable.isParameter;

public class Issue431Test extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testIsParameter() {
        myFixture.configureByFiles("planet.ex");
        PsiElement parameter = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling();

        assertInstanceOf(parameter, UnqualifiedNoArgumentsCall.class);
        assertTrue("planet is not marked as a parameter", isParameter(parameter));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_431";
    }
}
