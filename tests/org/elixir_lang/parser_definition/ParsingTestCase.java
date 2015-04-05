package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.psi.*;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luke.imhoff on 8/7/14.
 */
@org.junit.Ignore("abstract")
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        super("", "ex", new ElixirParserDefinition());
    }

    protected void assertParsedAndQuotedAroundError() {
        doTest(true);
        assertQuotedAroundError();
    }

    protected void assertParsedAndQuotedAroundExit() {
        doTest(true);
        assertQuotedAroundExit();
    }

    protected void assertParsedAndQuotedCorrectly() {
        doTest(true);
        assertQuotedCorrectly();
    }

    protected void assertParsedWithError() {
        doTest(true);

        final FileViewProvider fileViewProvider = myFile.getViewProvider();
        PsiFile root = fileViewProvider.getPsi(ElixirLanguage.INSTANCE);
        final List<PsiElement> errorElementList = new LinkedList<PsiElement>();

        root.accept(
                new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof PsiErrorElement) {
                            errorElementList.add(element);
                        }

                        super.visitElement(element);
                    }
                }
        );

        assertTrue("No PsiErrorElements found in parsed file PSI", !errorElementList.isEmpty());

        Quoter.assertError(myFile);
    }

    protected void assertQuotedAroundError() {
        assertInstanceOf(ElixirPsiImplUtil.quote(myFile), OtpErlangObject.class);
        Quoter.assertError(myFile);
    }

    protected void assertQuotedAroundExit() {
        assertInstanceOf(ElixirPsiImplUtil.quote(myFile), OtpErlangObject.class);
        Quoter.assertExit(myFile);
    }

    protected void assertQuotedCorrectly() {
        Quoter.assertQuotedCorrectly(myFile);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/parser_definition";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}
