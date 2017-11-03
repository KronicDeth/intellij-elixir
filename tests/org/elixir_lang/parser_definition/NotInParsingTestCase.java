package org.elixir_lang.parser_definition;

import org.elixir_lang.sdk.elixir.Release;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

public class NotInParsingTestCase extends ParsingTestCase {
    public void testIssue844() {
        if (elixirSdkRelease().compareTo(Release.V_1_5) >= 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertTrue(elixirSdkRelease().compareTo(Release.V_1_5) < 0);
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

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registerProjectFileIndex();
    }
}
