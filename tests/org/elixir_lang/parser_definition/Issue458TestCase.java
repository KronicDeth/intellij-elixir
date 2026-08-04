package org.elixir_lang.parser_definition;

public class Issue458TestCase extends ParsingTestCase {
    public void testNoNewline() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOneNewline() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultipleNewlines() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/issue_458";
    }
}
