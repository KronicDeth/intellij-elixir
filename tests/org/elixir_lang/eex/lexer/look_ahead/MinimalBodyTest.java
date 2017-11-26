package org.elixir_lang.eex.lexer.look_ahead;

import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;


/**
 * Tests that two tags parse as two tags instead of eating the outer opening and closing as a single tag
 */
@RunWith(Parameterized.class)
public class MinimalBodyTest extends Test {
    private static final Lex[] PREFIX = new Lex[]{
            new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE)
    };
    private static final Lex[][] INFIXES = new Lex[][]{
            {new Lex("body", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("#", Types.COMMENT_MARKER, Flex.COMMENT), new Lex("body", Types.COMMENT, Flex.COMMENT)},
            {new Lex("%", Types.QUOTATION_MARKER, Flex.QUOTATION), new Lex("body", Types.QUOTATION, Flex.QUOTATION)},
            {new Lex("/", Types.FORWARD_SLASH_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("|", Types.PIPE_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR)}
    };
    private static final Lex[] SUFFIX = new Lex[]{
            new Lex("%>", Types.CLOSING, Flex.YYINITIAL)
    };

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        Collection<Object> parameters = new ArrayList<>();

        for (Lex[] firstInfix : INFIXES) {
            for (Lex[] secondInfix : INFIXES) {
                Lex[] lexes = Stream
                        .of(PREFIX, firstInfix, SUFFIX, PREFIX, secondInfix, SUFFIX)
                        .flatMap(Stream::of)
                        .toArray(Lex[]::new);

                parameters.add(new Sequence(lexes));
            }
        }

        return parameters;
    }

    public MinimalBodyTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @org.junit.Test
    public void minimalBody() {
        assertSequence();
    }
}
