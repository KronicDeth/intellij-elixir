package org.elixir_lang.parser_definition;

import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.intellij_elixir.Quoter;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public abstract class ParsingTestCase extends com.intellij.testFramework.ParsingTestCase {
    public ParsingTestCase() {
        super("", "ex", new ElixirParserDefinition());
    }

    protected void assertQuotedCorrectly() {
        Quoter.assertQuotedCorrectly(myFile);
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
