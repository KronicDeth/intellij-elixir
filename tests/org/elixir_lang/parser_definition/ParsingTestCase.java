package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.intellij.lang.ParserDefinition;
import com.intellij.psi.*;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.psi.quoting.QuotingDialect;
import org.elixir_lang.psi.quoting.QuotingDialectResolver;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kadie.enheduanna.inanna on 8/7/14.
 */
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        this("ex", new ElixirParserDefinition());
    }

    protected ParsingTestCase(String extension, ParserDefinition... parserDefinitions) {
        super("", extension, parserDefinitions);
    }

    /**
     * Quotes in the dialect of the Elixir the reference quoter is running, so both sides of
     * {@link #assertQuotedCorrectly()} speak the same version.
     *
     * These are light fixtures with no Elixir SDK, so production resolution would reach
     * {@link QuotingDialect#FALLBACK} on every CI leg and every leg would compare against the same
     * dialect however old the Elixir it ran. {@code ELIXIR_VERSION} is exported to the test JVM by
     * the build, from the SDK it resolved - the same SDK the quoter was built against.
     *
     * Absent (a bare {@code ./gradlew test} outside the build's environment), the override is not
     * installed at all and resolution falls back as production would.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String elixirVersion = System.getenv("ELIXIR_VERSION");

        if (elixirVersion != null && !elixirVersion.isBlank()) {
            QuotingDialectResolver.overrideDialect(getProject(), QuotingDialect.of(elixirVersion));
        }
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

    /**
     * For a construct the parser accepts in every version, but that the reference quoter started
     * rejecting as invalid Elixir from {@code dialect} on - so only one side of the boundary can be
     * asserted per run, chosen by the dialect the leg's real Elixir resolves to (set in
     * {@link #setUp()}), not by which Elixir wrote the test.
     */
    protected void assertParsedAndQuotedCorrectlyBefore(QuotingDialect dialect) {
        assertParsedAndQuotedCorrectlyBefore(dialect, true);
    }

    protected void assertParsedAndQuotedCorrectlyBefore(QuotingDialect dialect, boolean checkResult) {
        doTest(checkResult);

        if (QuotingDialectResolver.dialectFor(myFile).compareTo(dialect) >= 0) {
            assertQuotedAroundError();
        } else {
            assertWithoutLocalError();
            assertQuotedCorrectly();
        }
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

        assertNotNull(root);
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
