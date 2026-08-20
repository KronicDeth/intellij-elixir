package org.elixir_lang.parser_definition;

import org.elixir_lang.psi.quoting.QuotingDialect;
import org.elixir_lang.psi.quoting.QuotingDialectResolver;

import java.io.IOException;

/**
 * `& 1` - the one construct whose parse tree depends on which Elixir the file belongs to.
 *
 * Both dialects are forced here rather than read from the leg's Elixir, so every leg covers both
 * shapes. A fixture that took the dialect from the environment would only ever exercise whichever
 * side of the 1.15.0 boundary that leg happened to run.
 *
 * Parse trees only, no quoting: the reference quoter runs at the leg's real version, so it cannot
 * confirm a shape that has been forced to a different one.
 */
public class CaptureArgumentParsingTestCase extends ParsingTestCase {
    /**
     * `&(1 + &2)` from Elixir 1.15.0, where the spaced `&` takes the whole expression, against
     * `&1 + &2` below it, where each spaced `&` takes only its own digit.
     */
    private static final String SOURCE = "& 1 + & 2\n";

    public void testSpacedCaptureArgumentBelow1_15() throws IOException {
        assertParsedInDialect(QuotingDialect.V1_13, "SpacedCaptureArgumentBelow1_15");
    }

    public void testSpacedCaptureArgumentFrom1_15() throws IOException {
        assertParsedInDialect(QuotingDialect.V1_15, "SpacedCaptureArgumentFrom1_15");
    }

    private void assertParsedInDialect(QuotingDialect dialect, String expectedName) throws IOException {
        QuotingDialectResolver.overrideDialect(getProject(), dialect);

        parseFile(expectedName, SOURCE);

        checkResult(expectedName, myFile);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/parser_definition/capture_argument_parsing_test_case";
    }
}
