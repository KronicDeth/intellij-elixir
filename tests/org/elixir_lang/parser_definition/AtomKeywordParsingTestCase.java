package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class AtomKeywordParsingTestCase extends ParsingTestCase {
    public void testFalse() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTrue() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/atom_keyword_parsing_test_case";
    }
}
