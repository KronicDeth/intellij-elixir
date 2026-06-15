package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body.sigil;

import org.elixir_lang.elixir_flex_lexer.Test;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Verifies that backslash sequences in non-interpolating sigil heredocs (~S, ~R …) are
 * treated as literal text, not as escape sequences.
 * <p>
 * Regression: GROUP_HEREDOC_LINE_BODY unconditionally entered ESCAPE_SEQUENCE on seeing '\',
 * ignoring isInterpolating().  For ~S"""…""" content (e.g. String's @moduledoc), this caused
 * `\\u{NNNNNN}` to produce ESCAPE + UNICODE_ESCAPE_CHARACTER + OPENING_CURLY +
 * BAD_CHARACTER×6 + CLOSING_CURLY, corrupting the PSI tree for the remainder of the file and
 * making every function defined after the doc (e.g. String.split/2) unresolvable.
 */
@RunWith(JUnit4.class)
        public class EscapeInHeredocTest extends Test {

    // ── helpers ────────────────────────────────────────────────────────────────────────

    /** Positions the lexer at GROUP_HEREDOC_LINE_BODY inside a NON-interpolating heredoc. */
    private void startNonInterpolating(String body) {
        lexer.start("~S'''\n" + body);
        lexer.advance(); // ~
        lexer.advance(); // S
        lexer.advance(); // '''
        lexer.advance(); // \n
    }

    /** Positions the lexer at GROUP_HEREDOC_LINE_BODY inside an interpolating heredoc. */
    private void startInterpolating(String body) {
        lexer.start("~s'''\n" + body);
        lexer.advance(); // ~
        lexer.advance(); // s
        lexer.advance(); // '''
        lexer.advance(); // \n
    }

    // ── non-interpolating (~S) ─────────────────────────────────────────────────────────

    @org.junit.Test
    public void backslashIsFragmentInNonInterpolatingHeredoc() {
        startNonInterpolating("\\");
        assertEquals(ElixirTypes.FRAGMENT, lexer.getTokenType());
        lexer.advance();
        assertNull(lexer.getTokenType());
    }

    /**
     * The primary regression test: \\u{NNNNNN} in a ~S heredoc must produce a single FRAGMENT
     * (all characters merged by MergingLexerAdapter), with NO BAD_CHARACTER tokens.
     * Before the fix, the lexer entered ESCAPE_SEQUENCE for '\', then UNICODE_ESCAPE_SEQUENCE
     * for 'u', then EXTENDED_HEXADECIMAL_ESCAPE_SEQUENCE for '{', and returned BAD_CHARACTER
     * for each 'N' because 'N' is not a hex digit.
     */
    @org.junit.Test
    public void unicodeCodePointEscapeIsFragmentInNonInterpolatingHeredoc() {
        startNonInterpolating("\\u{NNNNNN}");
        assertEquals(ElixirTypes.FRAGMENT, lexer.getTokenType());
        lexer.advance();
        assertNull("\\u{NNNNNN} in ~S heredoc must produce no further tokens (no BAD_CHARACTER)",
            lexer.getTokenType());
    }

    // ── interpolating (~s) ─────────────────────────────────────────────────────────────

    @org.junit.Test
    public void backslashIsEscapeInInterpolatingHeredoc() {
        startInterpolating("\\");
        assertEquals(ElixirTypes.ESCAPE, lexer.getTokenType());
    }

    /** Counterpart: \\u{AABB} in an interpolating heredoc IS processed as an escape sequence. */
    @org.junit.Test
    public void unicodeCodePointEscapeIsProcessedInInterpolatingHeredoc() {
        startInterpolating("\\u{AABB}");
        assertEquals(ElixirTypes.ESCAPE,                      lexer.getTokenType()); lexer.advance();
        assertEquals(ElixirTypes.UNICODE_ESCAPE_CHARACTER,    lexer.getTokenType()); lexer.advance();
        assertEquals(ElixirTypes.OPENING_CURLY,               lexer.getTokenType()); lexer.advance();
        assertEquals(ElixirTypes.VALID_HEXADECIMAL_DIGITS,    lexer.getTokenType()); lexer.advance();
        assertEquals(ElixirTypes.CLOSING_CURLY,               lexer.getTokenType()); lexer.advance();
        assertNull(lexer.getTokenType());
    }
}
