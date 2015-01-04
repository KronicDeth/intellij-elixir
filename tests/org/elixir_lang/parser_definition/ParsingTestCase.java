package org.elixir_lang.parser_definition;

import com.ericsson.otp.erlang.OtpErlangObject;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        super("", "ex", new ElixirParserDefinition());
    }

    protected void assertParsedAndQuotedAroundError() {
        doTest(true);
        assertQuotedAroundError();
    }

    protected void assertParsedAndQuotedCorrectly() {
        doTest(true);
        assertQuotedCorrectly();
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

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}
