package org.elixir_lang;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class ElixirTestCase extends TestCase {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.elixir_lang.parser_definition.GeneralParsingTestCase.class);
        suite.addTestSuite(org.elixir_lang.parser_definition.SingleQuotedStringParsingTestCase.class);

        return suite;
    }
}
