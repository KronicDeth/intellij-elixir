package org.elixir_lang.parser_definition;

import org.jetbrains.annotations.NotNull;

public class NotInParsingTestCase extends ParsingTestCase {
    public void testIssue844() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * Protected Instance Methods
     */

    @Override
    @NotNull
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/not_in_parsing_test_case";
    }
}
