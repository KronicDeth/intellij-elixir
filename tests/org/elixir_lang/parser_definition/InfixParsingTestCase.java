package org.elixir_lang.parser_definition;

import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightVirtualFile;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.psi.Operator;
import org.elixir_lang.psi.operation.Operation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class InfixParsingTestCase extends ParsingTestCase {
    /*
     * Static Methods
     */

    private static void assertOperator(Operation operation, @NotNull String operatorText) {
        assertEquals(operatorText, operation.operator().getText());
    }

    /*
     * Tests
     */

    public void testIssue251() {
        Operation[] operations = operations();

        assertEquals(1, operations.length);
        assertOperator(operations[0], "=");
    }

    public void testIssue251WithNoLeftOperand() {
        Operation[] operations = operations();

        assertEquals(2, operations.length);
        assertOperator(operations[0], "=");
        assertOperator(operations[1], "=");
    }

    public void testWellFormed() {
        Operation[] operations = operations();

        assertEquals(2, operations.length);

        assertOperator(operations[0], "=");
        assertOperator(operations[1], "=");
    }

    /*
     *
     * Instance Methods
     *   *
     */

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/infix_parsing_test_case";
    }

    @NotNull
    private Operation[] operations() {
        PsiFile root = root();
        Collection<Operation> operationCollection = PsiTreeUtil.findChildrenOfType(root, Operation.class);

        return operationCollection.toArray(new Operation[operationCollection.size()]);
    }

    @NotNull
    private PsiFile root() {
        String name = getTestName(false);
        PsiFile root;

        try {
            String text = loadFile(name + "." + myFileExt);
            myFile = createPsiFile(name, text);
            ensureParsed(myFile);
            assertEquals("light virtual file text mismatch", text, ((LightVirtualFile) myFile.getVirtualFile()).getContent().toString());
            assertEquals("virtual file text mismatch", text, LoadTextUtil.loadText(myFile.getVirtualFile()));
            assertEquals("doc text mismatch", text, myFile.getViewProvider().getDocument().getText());
            assertEquals("psi text mismatch", text, myFile.getText());
            FileViewProvider fileViewProvider = myFile.getViewProvider();
            root = fileViewProvider.getPsi(ElixirLanguage.INSTANCE);

            assertNotNull(root);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return root;
    }
}
