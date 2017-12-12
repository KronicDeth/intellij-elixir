package org.elixir_lang.parser_definition;

import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.Level.V_1_5;
import static org.elixir_lang.test.ElixirVersion.elixirSdkLevel;

public class NotInParsingTestCase extends ParsingTestCase {
    public void testIssue844() {
        boolean atLeastV_1_5 = elixirSdkLevel().compareTo(V_1_5) >= 0;

        if (atLeastV_1_5) {
            assertParsedAndQuotedCorrectly();
        } else {
            //noinspection ConstantConditions
            assertTrue(!atLeastV_1_5);
        }
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
