package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.psi.call.Call;

import static org.elixir_lang.reference.Callable.isVariable;

public class Issue422Test extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testIsVariable() {
        myFixture.configureByFiles("is_variable.ex");
        PsiElement callable = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling()
                .getLastChild()
                .getLastChild()
                .getLastChild()
                .getLastChild()
                .getLastChild()
                .getLastChild();

        assertInstanceOf(callable, Call.class);
        assertFalse("assoc is incorrectly marked as a variable", isVariable(callable));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_422";
    }
}
