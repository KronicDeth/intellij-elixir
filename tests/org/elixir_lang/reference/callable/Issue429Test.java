package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.psi.call.Call;

public class Issue429Test extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testUseScope() {
        myFixture.configureByFiles("get_use_scope.ex");
        PsiElement callable = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getPrevSibling()
                .getLastChild()
                .getLastChild()
                .getLastChild()
                .getLastChild()
                .getLastChild();

        assertInstanceOf(callable, Call.class);
        SearchScope useScope = callable.getUseScope();

        assertInstanceOf(useScope, LocalSearchScope.class);

        LocalSearchScope localSearchScope = (LocalSearchScope) useScope;
        PsiElement[] scope = localSearchScope.getScope();

        assertEquals(1, scope.length);
        PsiElement singleScope = scope[0];

        assertTrue(
                "Use Scope is not the surrounding if",
                singleScope.getText().startsWith("if auth == ")
        );
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_429";
    }
}
