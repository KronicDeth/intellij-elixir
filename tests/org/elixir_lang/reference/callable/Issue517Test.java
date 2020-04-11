package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall;

import static org.elixir_lang.reference.Callable.variableUseScope;

public class Issue517Test extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testVariableUseScope() {
        myFixture.configureByFiles("variable_use_scope.ex");
        @SuppressWarnings("ConstantConditions") PsiElement callable = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getParent();

        assertInstanceOf(callable, UnqualifiedNoArgumentsCall.class);
        assertEquals(
                LocalSearchScope.EMPTY,
                variableUseScope((UnqualifiedNoArgumentsCall) callable)
        );
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_517";
    }
}
