package org.elixir_lang.psi.operation;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.Operator;
import org.elixir_lang.psi.call.Call;

public class PrefixTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testPrimaryArgumentsWithZeroOperands() {
        myFixture.configureByFile("zero_operands.ex");
        PsiElement elementAt = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAt);

        PsiElement parent = elementAt.getParent();

        assertInstanceOf(parent, Operator.class);

        PsiElement grandParent = parent.getParent();

        assertInstanceOf(grandParent, Prefix.class);
        assertInstanceOf(grandParent, Call.class);

        Call grandParentPrefixCall = (Call) grandParent;
        PsiElement[] primaryArguments = grandParentPrefixCall.primaryArguments();

        assertNotNull(primaryArguments);
        assertEquals(1, primaryArguments.length);
        assertNull(primaryArguments[0]);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/psi/operation/prefix";
    }
}
