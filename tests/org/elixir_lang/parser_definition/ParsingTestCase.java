package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ParserDefinition;
import com.intellij.psi.*;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        this("ex", new ElixirParserDefinition());
    }

    protected ParsingTestCase(String extension, ParserDefinition... parserDefinitions) {
        super("", extension, parserDefinitions);
    }

    protected void assertParsedAndQuotedAroundError() {
        assertParsedAndQuotedAroundError(true);
    }

    protected void assertParsedAndQuotedAroundError(boolean checkResult) {
        doTest(checkResult);
        assertQuotedAroundError();
    }

    protected void assertParsedAndQuotedCorrectly() {
        assertParsedAndQuotedCorrectly(true);
    }

    protected void assertParsedAndQuotedCorrectly(boolean checkResult) {
        doTest(checkResult);
        assertWithoutLocalError();
        assertQuotedCorrectly();
    }

    protected void assertParsedWithErrors() {
        assertParsedWithErrors(true);
    }

    protected void assertParsedWithErrors(boolean checkResult) {
        doTest(checkResult);

        assertWithLocalError();
        Quoter.assertError(myFile);
    }

    private List<PsiElement> localErrors() {
        final FileViewProvider fileViewProvider = myFile.getViewProvider();
        PsiFile root = fileViewProvider.getPsi(ElixirLanguage.INSTANCE);
        final List<PsiElement> errorElementList = new LinkedList<>();

        root.accept(
                new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(@NotNull PsiElement element) {
                        if (element instanceof PsiErrorElement) {
                            errorElementList.add(element);
                        }

                        super.visitElement(element);
                    }
                }
        );

        return errorElementList;
    }

    protected void assertWithLocalError() {

        List<PsiElement> errorElementList = localErrors();

        assertFalse("No PsiErrorElements found in parsed file PSI", errorElementList.isEmpty());
    }

    protected void assertWithoutLocalError() {

        List<PsiElement> errorElementList = localErrors();

        assertTrue("PsiErrorElements found in parsed file PSI", errorElementList.isEmpty());
    }

    protected void assertQuotedAroundError() {
        assertInstanceOf(ElixirPsiImplUtil.quote(myFile), OtpErlangObject.class);
        Quoter.assertError(myFile);
    }

    protected void assertQuotedCorrectly() {
        Quoter.assertQuotedCorrectly(myFile);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/parser_definition";
    }

    /**
     * Whether test is running on travis-ci.
     *
     * @return {@code true} if on Travis CI; {@code false} otherwise
     */
    protected boolean isNotTravis() {
        String travis = System.getenv("TRAVIS");

        return travis == null || !travis.equals("true");
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

}
