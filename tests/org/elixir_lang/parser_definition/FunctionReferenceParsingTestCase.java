package org.elixir_lang.parser_definition;

import static org.elixir_lang.Level.V_1_5;
import static org.elixir_lang.test.ElixirVersion.elixirSdkLevel;

public class FunctionReferenceParsingTestCase extends ParsingTestCase {
    public void testAliasDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasDotOperator() {
        boolean beforeV_1_5 = elixirSdkLevel().compareTo(V_1_5) < 0;

        if (beforeV_1_5) {
            assertParsedAndQuotedCorrectly();
        } else {
            //noinspection ConstantConditions
            assertTrue(!beforeV_1_5);
        }
    }

    public void testAtomDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomDotOperator() {
        boolean beforeV_1_5 = elixirSdkLevel().compareTo(V_1_5) < 0;

        if (beforeV_1_5) {
            assertParsedAndQuotedCorrectly();
        } else {
            //noinspection ConstantConditions
            assertTrue(!beforeV_1_5);
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
