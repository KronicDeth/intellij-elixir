package org.elixir_lang.parser_definition;

public class Issue674TestCase extends ParsingTestCase {
    public void testFeymartynov() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/issue_674";
    }
}
