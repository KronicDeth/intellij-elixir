package org.elixir_lang.heex.lexer.look_ahead;

import org.elixir_lang.heex.lexer.Flex;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * HEEx-only: {@code {}} brace interpolation, which EEx does not have.
 *
 * {@code {} in YYINITIAL synthesizes a zero-length EQUALS_MARKER (the same trick MARKER_MAYBE uses
 * for a bodyless `<% %>` tag) so that the Elixir parser sees the body as if it were a `<%= %>` tag.
 * {@code openBraceCount} then lets `}` characters nest inside the interpolated Elixir without
 * closing the brace early.
 */
@RunWith(Parameterized.class)
public class BraceInterpolationTest extends Test {
    private static final Lex[][] SEQUENCES = new Lex[][]{
            // {@x}
            {
                    new Lex("{", Types.BRACE_OPENING, Flex.BEGIN_MATCHED_BRACES),
                    new Lex("", Types.EQUALS_MARKER, Flex.MATCHED_BRACES),
                    new Lex("@x", Types.ELIXIR, Flex.MATCHED_BRACES),
                    new Lex("}", Types.BRACE_CLOSING, Flex.YYINITIAL)
            },
            // {%{a: 1}} - a nested `{`/`}` pair does not close the outer brace early
            {
                    new Lex("{", Types.BRACE_OPENING, Flex.BEGIN_MATCHED_BRACES),
                    new Lex("", Types.EQUALS_MARKER, Flex.MATCHED_BRACES),
                    new Lex("%{a: 1}", Types.ELIXIR, Flex.MATCHED_BRACES),
                    new Lex("}", Types.BRACE_CLOSING, Flex.YYINITIAL)
            },
            // {@x (unbalanced, runs to EOF without closing - no crash, no BRACE_CLOSING emitted)
            {
                    new Lex("{", Types.BRACE_OPENING, Flex.BEGIN_MATCHED_BRACES),
                    new Lex("", Types.EQUALS_MARKER, Flex.MATCHED_BRACES),
                    new Lex("@x", Types.ELIXIR, Flex.MATCHED_BRACES)
            },
            // {\}} - an escaped `}` is an opaque two-character sequence that does not close the brace
            {
                    new Lex("{", Types.BRACE_OPENING, Flex.BEGIN_MATCHED_BRACES),
                    new Lex("", Types.EQUALS_MARKER, Flex.MATCHED_BRACES),
                    new Lex("\\}", Types.ELIXIR, Flex.MATCHED_BRACES),
                    new Lex("}", Types.BRACE_CLOSING, Flex.YYINITIAL)
            },
            // {\{} - an escaped `{` likewise does not open a nested brace
            {
                    new Lex("{", Types.BRACE_OPENING, Flex.BEGIN_MATCHED_BRACES),
                    new Lex("", Types.EQUALS_MARKER, Flex.MATCHED_BRACES),
                    new Lex("\\{", Types.ELIXIR, Flex.MATCHED_BRACES),
                    new Lex("}", Types.BRACE_CLOSING, Flex.YYINITIAL)
            },
            // {%{a: 1}\}} - the nested pair balances the count back to zero; the escaped `\}` after it
            // must not close the tag, only the final real `}` does.
            {
                    new Lex("{", Types.BRACE_OPENING, Flex.BEGIN_MATCHED_BRACES),
                    new Lex("", Types.EQUALS_MARKER, Flex.MATCHED_BRACES),
                    new Lex("%{a: 1}\\}", Types.ELIXIR, Flex.MATCHED_BRACES),
                    new Lex("}", Types.BRACE_CLOSING, Flex.YYINITIAL)
            }
    };

    public BraceInterpolationTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        return Arrays.stream(SEQUENCES).<Object>map(Sequence::new)::iterator;
    }

    @org.junit.Test
    public void braceInterpolation() {
        assertSequence();
    }
}
