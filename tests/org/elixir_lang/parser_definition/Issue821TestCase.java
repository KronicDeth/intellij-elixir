package org.elixir_lang.parser_definition;

public class Issue821TestCase extends ParsingTestCase {
    public void testEndKeywordKey() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/issue_821";
    }
}
