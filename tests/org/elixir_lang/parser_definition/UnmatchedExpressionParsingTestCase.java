package org.elixir_lang.parser_definition;

public class UnmatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testUnqualifiedNoArgumentsAfterBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsAfterStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsElseBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsElseStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabElseStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoArgumentsStabBlock() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/unmatched_expression_parsing_test_case";
    }
}
