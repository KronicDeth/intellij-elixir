package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import kotlin.ranges.IntRange;
import org.elixir_lang.NameArityRange;
import org.elixir_lang.psi.call.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.elixir_lang.psi.CallDefinitionClause.nameArityRange;

public class ImportTest extends LightPlatformCodeInsightFixtureTestCase {
    /*
     * Tests
     */

    public void testCallDefinitionClauseCallWhileImportModule() {
        myFixture.configureByFiles("import_module.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertTrue(Import.is(call));

        final ArrayList<Call> importedCallList = new ArrayList<>();

        Import.callDefinitionClauseCallWhile(call, call1 -> {
            importedCallList.add(call1);
            return true;
        });

        assertEquals(3, importedCallList.size());
    }

    public void testCallDefinitionClauseCallWhileImportModuleExceptNameArity() {
        myFixture.configureByFiles("import_module_except_name_arity.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertTrue(Import.is(call));

        final ArrayList<Call> importedCallList = new ArrayList<>();

        Import.callDefinitionClauseCallWhile(call, call1 -> {
            importedCallList.add(call1);
            return true;
        });

        assertEquals(2, importedCallList.size());

        List<NameArityRange> nameArityRangeList = ContainerUtil.map(
                importedCallList,
                CallDefinitionClause::nameArityRange
        );

        assertContainsElements(
                Arrays.asList(
                        new NameArityRange("imported", new IntRange(1, 1)),
                        new NameArityRange("imported", new IntRange(0, 0))
                ),
                nameArityRangeList
        );
    }

    public void testCallDefinitionClauseCallWhileImportModuleOnlyNameArity() {
        myFixture.configureByFiles("import_module_only_name_arity.ex", "imported.ex");
        PsiElement elementAtCaret = myFixture.getFile().findElementAt(myFixture.getCaretOffset());

        assertNotNull(elementAtCaret);

        PsiElement maybeCall = elementAtCaret.getParent().getParent();

        assertInstanceOf(maybeCall, Call.class);

        Call call = (Call) maybeCall;
        assertTrue(Import.is(call));

        final ArrayList<Call> importedCallList = new ArrayList<>();

        Import.callDefinitionClauseCallWhile(call, call1 -> {
            importedCallList.add(call1);
            return true;
        });

        assertEquals(1, importedCallList.size());

        Call importedCall = importedCallList.get(0);

        assertEquals(new NameArityRange("imported", new IntRange(0, 0)), nameArityRange(importedCall));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/psi/import";
    }
}
