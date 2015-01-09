package org.elixir_lang.elixir_flex_lexer.group.valid_escape_sequence;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 10/13/14.
 */
@RunWith(Theories.class)
public class BracedCharacterCodeTest extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Methods
     */

    private static CharSequence bracedCharacterCode(CharSequence digits) {
        return "\\x{" + digits + "}";
    }

    @DataPoints
    public static CharSequence[] dataPoints() {
        Vector<CharSequence> charSequences = new Vector<CharSequence>();

        // ensure all valid digits are covered for each length

        // 6 hexadecimal digits
        charSequences.add(bracedCharacterCode("123456"));
        charSequences.add(bracedCharacterCode("789012"));
        charSequences.add(bracedCharacterCode("ABCDEF"));
        charSequences.add(bracedCharacterCode("abcdef"));

        // 5 hexadecimal digits
        charSequences.add(bracedCharacterCode("12345"));
        charSequences.add(bracedCharacterCode("67890"));
        charSequences.add(bracedCharacterCode("ABCDE"));
        charSequences.add(bracedCharacterCode("FABCD"));
        charSequences.add(bracedCharacterCode("abcde"));
        charSequences.add(bracedCharacterCode("fabcd"));

        // 4 hexadecimal digits
        charSequences.add(bracedCharacterCode("1234"));
        charSequences.add(bracedCharacterCode("5678"));
        charSequences.add(bracedCharacterCode("9012"));
        charSequences.add(bracedCharacterCode("ABCD"));
        charSequences.add(bracedCharacterCode("EFAB"));
        charSequences.add(bracedCharacterCode("abcd"));
        charSequences.add(bracedCharacterCode("efab"));

        // 3 hexadecimal digits
        charSequences.add(bracedCharacterCode("120"));
        charSequences.add(bracedCharacterCode("123"));
        charSequences.add(bracedCharacterCode("456"));
        charSequences.add(bracedCharacterCode("789"));
        charSequences.add(bracedCharacterCode("ABC"));
        charSequences.add(bracedCharacterCode("DEF"));
        charSequences.add(bracedCharacterCode("abc"));
        charSequences.add(bracedCharacterCode("def"));

        // 2 hexadecimal digits
        charSequences.add(bracedCharacterCode("12"));
        charSequences.add(bracedCharacterCode("34"));
        charSequences.add(bracedCharacterCode("56"));
        charSequences.add(bracedCharacterCode("78"));
        charSequences.add(bracedCharacterCode("90"));
        charSequences.add(bracedCharacterCode("AB"));
        charSequences.add(bracedCharacterCode("CD"));
        charSequences.add(bracedCharacterCode("EF"));
        charSequences.add(bracedCharacterCode("ab"));
        charSequences.add(bracedCharacterCode("cd"));
        charSequences.add(bracedCharacterCode("ef"));

        // 1 hexadecimal digit
        charSequences.add(bracedCharacterCode("1"));
        charSequences.add(bracedCharacterCode("2"));
        charSequences.add(bracedCharacterCode("3"));
        charSequences.add(bracedCharacterCode("4"));
        charSequences.add(bracedCharacterCode("5"));
        charSequences.add(bracedCharacterCode("6"));
        charSequences.add(bracedCharacterCode("7"));
        charSequences.add(bracedCharacterCode("8"));
        charSequences.add(bracedCharacterCode("9"));
        charSequences.add(bracedCharacterCode("0"));

        return charSequences.toArray(new CharSequence[charSequences.size()]);
    };

    protected void reset(CharSequence charSequence) throws IOException {
        // start of "\"" + promoter to trigger GROUP state
        CharSequence fullCharSequence = "\"" + charSequence;
        super.reset(fullCharSequence);
        // consume "
        flexLexer.advance();
    }

    @Theory
    public void validCharacterCode(CharSequence charSequence) throws IOException {
        reset(charSequence);

        assertEquals(ElixirTypes.ESCAPE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    };
}
