package org.elixir_lang.structure_view.element;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;

import java.lang.Exception;

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

        assertTrue("Call at caret is not a call definition clause", CallDefinitionClause.is(call));
        PsiElement head = CallDefinitionClause.head(call);

        assertNotNull("Call definition has a null head", head);

        assertEquals(
                "create(state = %__MODULE__{ecto_schema_module: ecto_schema_module, view: view}, params)",
                CallDefinitionHead.stripGuard(head).getText()
        );
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        if (myFixture != null) {
            Project project = getProject();

            if (project != null && !project.isDisposed()) {
                Disposer.dispose(project);
            }
        }
    }
}
