package org.elixir_lang.parser_definition.triple_double_quotes;

import org.elixir_lang.parser_definition.triple_double_quotes.ParsingTestCase;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class BodyParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testEscapeSequences() {
        doTest(true);
    }

    public void testWithInterpolation() {
        doTest(true);
    }

    public void testMinimal() {
        doTest(true);
    }

    public void testWhitespaceEndPrefix() {
        doTest(true);
    }

    public void testWithNestedInterpolation() {
        doTest(true);
    }

    public void testWithoutInterpolation() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/body_parsing_test_case";
    }
}
