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
    private static final Lex[][] TAGS = new Lex[][]{
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("", Types.EMPTY_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("#", Types.COMMENT_MARKER, Flex.COMMENT), new Lex("body", Types.COMMENT, Flex.COMMENT), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%%", Types.ESCAPED_OPENING, Flex.YYINITIAL), new Lex("body%>", Types.DATA, Flex.YYINITIAL)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("/", Types.FORWARD_SLASH_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("|", Types.PIPE_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR), new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE)}
    };

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        Collection<Object> parameters = new ArrayList<>();

        for (Lex[] firstTag : TAGS) {
            for (Lex[] secondTag : TAGS) {
                Lex[] lexes = Stream
                        .of(firstTag, secondTag)
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
