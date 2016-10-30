package org.elixir_lang.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ChooseByNameRegistry;
import com.intellij.navigation.NavigationItem;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionClause;

public class GotoSymbolContributorTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testIssue472() {
        myFixture.configureByFile("issue_472.ex");
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

        NavigationItem[] itemsByName = gotoSymbolContributor.getItemsByName(
                "decode_auth_type",
                "decode_a",
                myFixture.getProject(),
                false
        );

        assertEquals(2, itemsByName.length);

        assertInstanceOf(itemsByName[0], CallDefinition.class);
        CallDefinition callDefinition = (CallDefinition) itemsByName[0];
        assertEquals("decode_auth_type", callDefinition.name());

        assertInstanceOf(itemsByName[1], CallDefinitionClause.class);
        CallDefinitionClause callDefinitionClause = (CallDefinitionClause) itemsByName[1];
        assertEquals("decode_auth_type", callDefinitionClause.getName());
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/navigation/goto_symbol_contributor";
    }
}
