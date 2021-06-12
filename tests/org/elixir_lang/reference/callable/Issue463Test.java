package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.xml.Resolve;
import org.elixir_lang.psi.CallDefinitionClause;
import org.elixir_lang.psi.call.Call;

public class Issue463Test extends BasePlatformTestCase {
    /*
     * Tests
     */

    public void testDirectModuleQualifier() {
        myFixture.configureByFiles("direct_module_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);

        assertInstanceOf(resolved, Call.class);
        assertEquals(
                "def changeset(params) do\n" +
                "    %__MODULE__{}\n" +
                "    |> cast(params, ~w(name))\n" +
                "    |> validate_required(:name)\n" +
                "  end",
                resolved.getText()
        );

        assertInstanceOf(resolved, Call.class);

        assertTrue(CallDefinitionClause.is((Call) resolved));
    }

    public void testAliasedModuleQualifier() {
        myFixture.configureByFiles("aliased_module_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);

        assertInstanceOf(resolved, Call.class);
        assertEquals(
                "def changeset(params) do\n" +
                        "    %__MODULE__{}\n" +
                        "    |> cast(params, ~w(name))\n" +
                        "    |> validate_required(:name)\n" +
                        "  end",
                resolved.getText()
        );

        assertInstanceOf(resolved, Call.class);

        assertTrue(CallDefinitionClause.is((Call) resolved));
    }

    public void testDoubleAliasesModuleQualifier() {
        myFixture.configureByFiles("double_aliased_module_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);
        assertEquals(resolved.getText(), "def changeset(params) do\n" +
                "    %__MODULE__{}\n" +
                "    |> cast(params, ~w(name))\n" +
                "    |> validate_required(:name)\n" +
                "  end");
    }

    public void testMapAccessQualifier() {
        myFixture.configureByFiles("map_access_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNull(resolved);
    }

    public void testUnresolvedAliasQualifier() {
        myFixture.configureByFiles("unresolved_alias_qualifier.ex", "referenced.ex");
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference psiPolyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = psiPolyVariantReference.multiResolve(false);

        assertEquals(1, resolveResults.length);

        ResolveResult firstResolveResult = resolveResults[0];

        assertFalse(firstResolveResult.isValidResult());

        PsiElement firstElement = firstResolveResult.getElement();

        assertNotNull(firstElement);

        assertEquals(firstElement.getText(), "def changeset(params) do\n" +
                "    %__MODULE__{}\n" +
                "    |> cast(params, ~w(name))\n" +
                "    |> validate_required(:name)\n" +
                "  end");
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_463";
    }
}
