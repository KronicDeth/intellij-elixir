package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class matchedNonNumericAtOperationParsingTestCase extends ParsingTestCase {
    public void testNoParenthesesManyArgumentsQualifiedCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesManyArgumentsUnqualifiedCall() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_non_numeric_at_operation_parsing_test_case";
    }
}
