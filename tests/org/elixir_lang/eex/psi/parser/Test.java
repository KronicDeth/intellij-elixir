package org.elixir_lang.eex.psi.parser;

import org.elixir_lang.eex.ParserDefinition;
import org.elixir_lang.parser_definition.ParsingTestCase;

import java.io.IOException;

public class Test extends ParsingTestCase {
    public Test() {
        super("eex", new ParserDefinition());
    }

    public void testEExTemplate() {
        doTest(true);
    }

    public void testEExTemplateWithBindings() {
        doTest(true);
    }

    public void testStringSample() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/eex/psi/parser/test";
    }
}
