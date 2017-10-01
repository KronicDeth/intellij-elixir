package org.elixir_lang.parser_definition;

public class Issue821 extends ParsingTestCase {
    public void testEndKeywordKey() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/issue_821";
    }
}
