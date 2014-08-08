package org.elixir_lang;

import com.intellij.testFramework.ParsingTestCase;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class ElixirParsingTest extends ParsingTestCase {
    public ElixirParsingTest() {
        super("", "ex", new ElixirParserDefinition());
    }

    public void testBadCharacterPrefix() {
        doTest(true);
    }

    public void testBlankPrefix() {
        doTest(true);
    }

    public void testCommentAfterNumber() {
        doTest(true);
    }

    public void testComments() {
        doTest(true);
    }

    public void testEmpty() {
        doTest(true);
    }

    public void testEmptySingleQuotedString() {
        doTest(true);
    }

    public void testMultipleNumbersOnLine() {
        doTest(true);
    }

    public void testMultipleStringsOnLine() {
        doTest(true);
    }

    public void testNoEOLAtEOF() {
        doTest(true);
    }

    public void testRealistic() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
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
