package org.elixir_lang.structure_view.element;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.semantic.call.definition.Clause;
import org.elixir_lang.structure_view.element.call.definition.delegation.Head;

public class CallDefinitionHeadTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testIssue468() {
        myFixture.configureByFile("issue_468.ex");
        assertIssue468CallDefinitionHeadClauseHead();
    }

    public void testSingleWhen() {
        myFixture.configureByFile("single_when.ex");
        assertIssue468CallDefinitionHeadClauseHead();
    }

    public void testNoWhen() {
        myFixture.configureByFile("no_when.ex");
        assertIssue468CallDefinitionHeadClauseHead();
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/structure_view/element/call_definition_head";
    }

    /*
     * Private Instance Methods
     */

    private void assertIssue468CallDefinitionHeadClauseHead() {
        PsiElement element = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset())
                .getParent()
                .getParent();

        assertNotNull(element);
        assertInstanceOf(element, Call.class);

        Call call = (Call) element;

        assertTrue("Call at caret is not a call definition clause", Clause.is(call));
        PsiElement head = Clause.head(call);

        assertNotNull("Call definition has a null head", head);

        assertEquals(
                "create(state = %__MODULE__{ecto_schema_module: ecto_schema_module, view: view}, params)",
                Head.Companion.stripGuard(head).getText()
        );
    }
}
