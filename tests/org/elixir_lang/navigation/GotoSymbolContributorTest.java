package org.elixir_lang.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ChooseByNameRegistry;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.modular.Modular;

public class GotoSymbolContributorTest extends LightPlatformCodeInsightFixtureTestCase {
    private static GotoSymbolContributor gotoSymbolContributor() {
        ChooseByNameContributor[] symbolModelContributors = ChooseByNameRegistry
                .getInstance()
                .getSymbolModelContributors();

        GotoSymbolContributor gotoSymbolContributor = null;

        for (ChooseByNameContributor symbolModelContributor : symbolModelContributors) {
            if (symbolModelContributor instanceof GotoSymbolContributor) {
                gotoSymbolContributor = (GotoSymbolContributor) symbolModelContributor;
            }
        }

        assertNotNull(gotoSymbolContributor);

        return gotoSymbolContributor;
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/navigation/goto_symbol_contributor";
    }

    /*
     * Tests
     */

    public void testIssue472() {
        myFixture.configureByFile("issue_472.ex");
        GotoSymbolContributor gotoSymbolContributor = gotoSymbolContributor();
        NavigationItem[] itemsByName = gotoSymbolContributor.getItemsByName(
                "decode_auth_type",
                "decode_a",
                myFixture.getProject(),
                false
        );

        assertEquals(1, itemsByName.length);

        assertInstanceOf(itemsByName[0], CallDefinitionClause.class);
        CallDefinitionClause callDefinitionClause = (CallDefinitionClause) itemsByName[0];
        assertEquals("decode_auth_type", callDefinitionClause.getName());
    }

    public void testIssue705BeforeCompile() {
        myFixture.configureByFile("issue_705__before_compile__.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Call call = (Call) elementAtCaret.getParent().getParent().getParent().getParent();

        Call enclosingModularMacroCall = CallDefinitionClause.Companion.enclosingModularMacroCall(call);
        assertNotNull(enclosingModularMacroCall);

        Modular modular = CallDefinitionClause.Companion.enclosingModular(call);
        assertNotNull(modular);
    }

    public void testIssue705TypeFunctions() {
        myFixture.configureByFile("issue_705_type_functions.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Call call = (Call) elementAtCaret.getParent().getParent().getParent();

        Call enclosingModularMacroCall = CallDefinitionClause.Companion.enclosingModularMacroCall(call);
        assertNotNull(enclosingModularMacroCall);

        Modular modular = CallDefinitionClause.Companion.enclosingModular(call);
        assertNotNull(modular);
    }

    public void testIssue1141() {
        myFixture.configureByFile("issue_1141.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Call call = (Call) elementAtCaret.getParent().getParent().getParent().getParent();

        Call enclosingModularMacroCall = CallDefinitionClause.Companion.enclosingModularMacroCall(call);
        assertNotNull(enclosingModularMacroCall);

        Modular modular = CallDefinitionClause.Companion.enclosingModular(call);
        assertNotNull(modular);
    }

    public void testIssue1228() {
        myFixture.configureByFile("issue_1228.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        Call call = (Call) elementAtCaret.getParent().getParent().getParent().getParent();

        Call enclosingModularMacroCall = CallDefinitionClause.Companion.enclosingModularMacroCall(call);
        assertNotNull(enclosingModularMacroCall);

        Modular modular = CallDefinitionClause.Companion.enclosingModular(call);
        assertNotNull(modular);
    }
}
