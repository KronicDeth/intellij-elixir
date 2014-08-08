package org.elixir_lang.parser_definition;

import org.elixir_lang.ElixirParserDefinition;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        super("", "ex", new ElixirParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/parser_definition";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}
