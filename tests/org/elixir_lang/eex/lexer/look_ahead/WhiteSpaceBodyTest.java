package org.elixir_lang.eex.lexer.look_ahead;

import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class WhiteSpaceBodyTest extends Test {
    private static final Lex[][] TAGS = new Lex[][]{
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("", Types.EMPTY_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("#", Types.COMMENT_MARKER, Flex.COMMENT), new Lex(" ", Types.COMMENT, Flex.COMMENT), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%%", Types.ESCAPED_OPENING, Flex.YYINITIAL), new Lex(" %>", Types.DATA, Flex.YYINITIAL)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("/", Types.FORWARD_SLASH_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("|", Types.PIPE_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)}
    };

    public WhiteSpaceBodyTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        return Arrays.stream(TAGS).<Object>map(Sequence::new)::iterator;
    }

    @org.junit.Test
    public void whiteSpaceBody() {
        assertSequence();
    }
}
