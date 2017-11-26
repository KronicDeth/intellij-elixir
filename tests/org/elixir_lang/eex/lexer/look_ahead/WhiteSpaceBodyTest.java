package org.elixir_lang.eex.lexer.look_ahead;

import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class WhiteSpaceBodyTest extends Test {
    private static final Lex[] PREFIX = new Lex[]{
            new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE)
    };
    private static final Lex[][] INFIXES = new Lex[][]{
            {new Lex(" ", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("#", Types.COMMENT_MARKER, Flex.COMMENT), new Lex(" ", Types.COMMENT, Flex.COMMENT)},
            {new Lex("%", Types.QUOTATION_MARKER, Flex.QUOTATION), new Lex(" ", Types.QUOTATION, Flex.QUOTATION)},
            {new Lex("/", Types.FORWARD_SLASH_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("|", Types.PIPE_MARKER, Flex.ELIXIR), new Lex(" ", Types.ELIXIR, Flex.ELIXIR)}
    };
    private static final Lex[] SUFFIX = new Lex[]{
            new Lex("%>", Types.CLOSING, Flex.YYINITIAL)
    };

    public WhiteSpaceBodyTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        return Arrays.stream(INFIXES).<Object>map(infix -> {
            Lex[] lexes = Stream.of(PREFIX, infix, SUFFIX).flatMap(Stream::of).toArray(Lex[]::new);

            return new Sequence(lexes);
        })::iterator;
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        super.start(charSequence + " %>");
    }

    @org.junit.Test
    public void whiteSpaceBody() {
        assertSequence();
    }
}
