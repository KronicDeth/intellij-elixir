package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class DualOperatorParsingTestCase extends ParsingTestCase {
    public void testIdentifierOperatorEOLIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierOperatorIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierSpaceOperatorIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierSpaceOperatorSpaceIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/dual_operator_parsing_test_case";
    }
}
