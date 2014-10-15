package org.elixir_lang.elixir_flex_lexer.group.valid_escape_sequence;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 10/14/14.
 */
@RunWith(Theories.class)
public class CharacterCodeTest extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Constants
     */

    private static final char FIRST_DECIMAL_DIGIT = '0';
    private static final char LAST_DECIMAL_DIGIT = '9';
    private static final int DECIMAL_DIGIT_COUNT = LAST_DECIMAL_DIGIT - FIRST_DECIMAL_DIGIT + 1;
    private static final char FIRST_LOWER_CASE_HEX_DIGIT = 'a';
    private static final char LAST_LOWER_CASE_HEX_DIGIT = 'f';
    private static final int LOWER_CASE_HEX_DIGIT_COUNT = LAST_LOWER_CASE_HEX_DIGIT - FIRST_LOWER_CASE_HEX_DIGIT + 1;
    private static final char FIRST_UPPER_CASE_HEX_DIGIT = 'A';
    private static final char LAST_UPPER_CASE_HEX_DIGIT = 'F';
    private static final int UPPER_CASE_HEX_DIGIT_COUNT = LAST_UPPER_CASE_HEX_DIGIT - FIRST_UPPER_CASE_HEX_DIGIT + 1;

    /*
     * Methods
     */

    private static List<Character> digits() {
        List<Character> digits = new ArrayList<Character>(DECIMAL_DIGIT_COUNT + LOWER_CASE_HEX_DIGIT_COUNT + UPPER_CASE_HEX_DIGIT_COUNT);

        for (char digit = FIRST_DECIMAL_DIGIT; digit <= LAST_DECIMAL_DIGIT; digit++) {
            digits.add(digit);
        }

        for (char digit = FIRST_LOWER_CASE_HEX_DIGIT; digit <= LAST_LOWER_CASE_HEX_DIGIT; digit++) {
            digits.add(digit);
        }

        for (char digit = FIRST_UPPER_CASE_HEX_DIGIT; digit <= LAST_UPPER_CASE_HEX_DIGIT; digit++) {
            digits.add(digit);
        }

        return digits;
    }

    @DataPoints
    public static CharSequence[] dataPoints() {
        List<Character> localDigits = digits();
        final int size = localDigits.size();
        List<CharSequence> charSequences = new ArrayList<CharSequence>((int) Math.pow(size, 2) + size);

        for (char highDigit : localDigits) {
            for (char lowDigit : localDigits) {
                charSequences.add("\\x" + highDigit + lowDigit);
            }
        }

        for (char digit : localDigits) {
            charSequences.add("\\x" + digit);
        }

        return charSequences.toArray(new CharSequence[charSequences.size()]);
    }

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

        assertEquals(ElixirTypes.VALID_ESCAPE_SEQUENCE, flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    };
}
