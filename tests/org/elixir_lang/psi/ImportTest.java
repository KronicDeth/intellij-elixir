package org.elixir_lang.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;


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

        final ArrayList importedCallList = new ArrayList<Call>();

        Import.callDefinitionClauseCallWhile(call, new Function<Call, Boolean>() {
            @Override
            public Boolean fun(Call call) {
                importedCallList.add(call);
                return true;
            }
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

        final ArrayList<Call> importedCallList = new ArrayList<Call>();

        Import.callDefinitionClauseCallWhile(call, new Function<Call, Boolean>() {
            @Override
            public Boolean fun(Call call) {
                importedCallList.add(call);
                return true;
            }
        });

        assertEquals(2, importedCallList.size());

        List<Pair<String, IntRange>> nameArityRangeList = ContainerUtil.map(
                importedCallList,
                new Function<Call, Pair<String, IntRange>>() {
                    @Override
                    public Pair<String, IntRange> fun(Call call) {
                        return nameArityRange(call);
                    }
                }
        );

        assertContainsElements(
                Arrays.asList(
                        pair("imported", new IntRange(1)),
                        pair("imported", new IntRange(0))
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

        final ArrayList<Call> importedCallList = new ArrayList<Call>();

        Import.callDefinitionClauseCallWhile(call, new Function<Call, Boolean>() {
            @Override
            public Boolean fun(Call call) {
                importedCallList.add(call);
                return true;
            }
        });

        assertEquals(1, importedCallList.size());

        Call importedCall = importedCallList.get(0);

        assertEquals(pair("imported", new IntRange(0)), nameArityRange(importedCall));
    }

    /*
     * Protected Instance Methods
     */

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/psi/import";
    }
}
