package org.elixir_lang.reference.callable;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.elixir_lang.psi.call.Call;

public class ImportTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testImportModule() {
        myFixture.configureByFiles("import_module.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertEquals("imported", call.functionName());
        assertEquals(0, call.resolvedFinalArity());

        PsiReference reference = call.getReference();

        assertNotNull(reference);
        assertInstanceOf(reference, PsiPolyVariantReference.class);

        PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;

        ResolveResult[] resolveResults = polyVariantReference.multiResolve(false);

        assertEquals(2, resolveResults.length);
    }

    public void testImportModuleOnlyNameArity() {

    }

    public void testImportModuleExceptNameArity() {

    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/reference/callable/import";
    }
}
