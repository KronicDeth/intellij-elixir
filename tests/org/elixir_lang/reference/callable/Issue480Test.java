package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import kotlin.ranges.IntRange;
import org.elixir_lang.NameArityRange;
import org.elixir_lang.psi.ElixirIdentifier;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class Issue480Test extends LightCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testLocal() {
        myFixture.configureByFiles("local.ex", "referenced.ex");
        assertReferenceAndResolvedNameArityRange("changeset", 2);
    }

    public void testRemote() {
        myFixture.configureByFiles("remote.ex", "referenced.ex");
        assertUnresolvableReferenceNameArityRange("changeset", 1);
    }

    public void testDirectModuleQualifier() {
        myFixture.configureByFiles("direct_module_qualifier.ex", "referenced.ex");
        assertReferenceAndResolvedNameArityRange("changeset", 1);
    }

    public void testAliasedModuleQualifier() {
        myFixture.configureByFiles("aliased_module_qualifier.ex", "referenced.ex");
        assertReferenceAndResolvedNameArityRange("changeset", 1);
    }

    public void testDoubleAliasesModuleQualifier() {
        myFixture.configureByFiles("double_aliased_module_qualifier.ex", "referenced.ex");
        assertUnresolvableReferenceNameArityRange("changeset", 1);
    }

    public void testMapAccessQualifier() {
        myFixture.configureByFiles("map_access_qualifier.ex", "referenced.ex");
        assertUnresolvableReferenceNameArityRange("__struct__", 1);
    }

    public void testUnresolvedAliasQualifier() {
        myFixture.configureByFiles("unresolved_alias_qualifier.ex", "referenced.ex");
        assertUnresolvableReferenceNameArityRange("changeset", 1);
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/issue_480";
    }

    /*
     * Private Instance Methods
     */

    private void assertUnresolvableReferenceNameArityRange(@NotNull String name, int arity) {
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        Call grandParentCall = (Call) grandParent;

        assertEquals(name, grandParentCall.functionName());
        assertEquals(arity, grandParentCall.resolvedFinalArity());

        PsiReference reference = grandParent.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNull(resolved);
    }

    private void assertReferenceAndResolvedNameArityRange(@NotNull String name, int arity) {
        PsiElement elementAtCaret = myFixture
                .getFile()
                .findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement grandParent = elementAtCaret.getParent().getParent();

        assertNotNull(grandParent);
        assertInstanceOf(grandParent, Call.class);

        Call grandParentCall = (Call) grandParent;

        assertEquals(name, grandParentCall.functionName());
        assertEquals(arity, grandParentCall.resolvedFinalArity());

        assertResolvedNameArityRange(grandParentCall, name, arity);
    }

    private void assertResolvedNameArityRange(@NotNull PsiElement element, @NotNull String name, int arity) {
        PsiReference reference = element.getReference();

        assertNotNull(reference);

        PsiElement resolved = reference.resolve();

        assertNotNull(resolved);

        assertInstanceOf(resolved, ElixirIdentifier.class);

        PsiElement maybeDefMaybeCall = resolved.getParent().getParent().getParent();
        assertInstanceOf(maybeDefMaybeCall, Call.class);

        Call maybeDefCall = (Call) maybeDefMaybeCall;

        assertTrue(CallDefinitionClause.Companion.is(maybeDefCall));

        assertEquals(new NameArityRange(name, new IntRange(arity, arity)), nameArityRange(maybeDefCall));
    }
}
