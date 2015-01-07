package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class CharListHeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testEscapeSequences() {
        doTest(true);
    }

    public void testMinimal() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhitespaceEndPrefix() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/char_list_heredoc_parsing_test_case";
    }
}
