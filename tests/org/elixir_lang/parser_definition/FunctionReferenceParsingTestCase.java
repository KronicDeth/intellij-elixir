package org.elixir_lang.parser_definition;

import org.elixir_lang.sdk.elixir.Release;

import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

public class FunctionReferenceParsingTestCase extends ParsingTestCase {
    public void testAliasDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasDotOperator() {
        if (elixirSdkRelease().compareTo(Release.V_1_5) < 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertTrue(elixirSdkRelease().compareTo(Release.V_1_5) >= 0);
        }
    }

    public void testAtomDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomDotOperator() {
        if (elixirSdkRelease().compareTo(Release.V_1_5) < 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertTrue(elixirSdkRelease().compareTo(Release.V_1_5) >= 0);
        }
    }

    public void testIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOperator() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/function_reference_parsing_test_case";
    }
}
