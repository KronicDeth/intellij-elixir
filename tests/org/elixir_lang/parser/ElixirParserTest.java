package org.elixir_lang.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ElixirParserTest {
    /*
     * CONSTANTS
     */

    private static final Pattern IMPORT_PATTERN = Pattern.compile("import .+\\.GeneratedParserUtilBase");

    /*
     * Private Static Methods
     */

    @Nullable
    private static String importLine(@NotNull File file) throws IOException {
       // See http://stackoverflow.com/a/5868528/470451
        BufferedReader parserBufferedReader = new BufferedReader(new FileReader(file));
        String importLine = null;

        for(String line; (line = parserBufferedReader.readLine()) != null; ) {
            Matcher matcher = IMPORT_PATTERN.matcher(line);

            if (matcher.lookingAt()) {
                importLine = line;

                break;
            }
        }

        return importLine;
    }

    /*
     * Tests
     */

    @Test
    public void testUsesEllixirLangGeneratedParserUtilBase() throws IOException {
        File parserFile = new File("gen/org/elixir_lang/parser/ElixirParser.java");

        assertTrue("Parser file does not exist", parserFile.exists());

        String importLine = importLine(parserFile);

        assertNotNull("Parser file has no import of GeneratedParserUtilBase", importLine);

        assertNotEquals(
                "Parser was regenerated without manually overriding import of GeneratedParserUtilBase.  " +
                        "Change line (`" + importLine + "`) to " +
                        "`import static org.elixir_lang.grammar.parser.GeneratedParserUtilBase.*;` " +
                        "and ensure org.elixir_lang.grammar.parser.GeneratedParserUtilBase is synced to the version " +
                        "from GrammarKit.",
                "import static com.intellij.lang.parser.GeneratedParserUtilBase.*;",
                importLine
        );
        assertEquals("import static org.elixir_lang.grammar.parser.GeneratedParserUtilBase.*;", importLine);
    }
}
