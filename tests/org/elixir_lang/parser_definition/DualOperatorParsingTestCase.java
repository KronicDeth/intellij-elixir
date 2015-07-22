package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class DualOperatorParsingTestCase extends ParsingTestCase {
    public void testIdentifierOperatorEOLIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierOperatorIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /**
     * [one-two, three] can be confused for [one(-two, three)] because the no parentheses many arguments is part of
     * matchedExpression even though it's not part of matched_expr.
     */
    public void testIdentifierOperatorIdentifierCommaIdentifierInContainer() {
        assertParsedAndQuotedCorrectly();
    }

    /**
     * see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L609
     */
    public void testIdentifierSpaceOperatorContainer() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierSpaceOperatorEOLIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierSpaceOperatorIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /**
     * see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_tokenizer.erl#L610
     */
    public void testIdentifierSpaceOperatorOperatorOperand() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierSpaceOperatorSpaceCommentEOLIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierSpaceOperatorSpaceIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/dual_operator_parsing_test_case";
    }
}
