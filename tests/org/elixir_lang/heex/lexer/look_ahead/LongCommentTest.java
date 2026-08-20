package org.elixir_lang.heex.lexer.look_ahead;

import org.elixir_lang.heex.lexer.Flex;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * {@code <%!-- ... --%>} HEEx comments. Matched with a dedicated {@code LONG_COMMENT_OPENING} rule
 * so the 2-char {@code OPENING} rule never gets a chance at the leading {@code <%}: JFlex's longest-
 * match picks the 5-char rule, which then pushes 3 characters ({@code !--}) back so {@code OPENING}
 * still returns just {@code <%} - keeping the existing {@code eexCommentBody ::= EEX_COMMENT_MARKER
 * EEX_COMMENT?} shape on the Elixir side, where {@code !--} is simply a longer
 * {@link Types#COMMENT_MARKER} than {@code #}.
 */
@RunWith(Parameterized.class)
public class LongCommentTest extends Test {
    private static final Lex[][] SEQUENCES = new Lex[][]{
            // Body containing "%>" that is not "--%>" does not close the comment early - only the
            // literal 4-character "--%>" does.
            {
                    new Lex("<%", Types.OPENING, Flex.LONG_COMMENT_MARKER),
                    new Lex("!--", Types.COMMENT_MARKER, Flex.LONG_COMMENT),
                    new Lex(" a %> comment ", Types.COMMENT, Flex.LONG_COMMENT),
                    new Lex("--%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)
            },
            // Unterminated: runs to EOF with no CLOSING token, same shape as
            // BraceInterpolationTest's unbalanced-brace case.
            {
                    new Lex("<%", Types.OPENING, Flex.LONG_COMMENT_MARKER),
                    new Lex("!--", Types.COMMENT_MARKER, Flex.LONG_COMMENT),
                    new Lex(" unterminated", Types.COMMENT, Flex.LONG_COMMENT)
            }
    };

    public LongCommentTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        return Arrays.stream(SEQUENCES).<Object>map(Sequence::new)::iterator;
    }

    @org.junit.Test
    public void longComment() {
        assertSequence();
    }
}
