package org.elixir_lang.eex.lexer.look_ahead;

import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Tests that two tags parse as two tags instead of eating the outer opening and closing as a single tag
 */
@RunWith(Parameterized.class)
public class MinimalBodyTest extends Test {
    private static final Lex[][] PREFIXES = new Lex[][]{
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("body", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("<%#", Types.OPENING_COMMENT, Flex.COMMENT), new Lex("body", Types.COMMENT, Flex.COMMENT)},
            {new Lex("<%%", Types.OPENING_QUOTATION, Flex.QUOTATION), new Lex("body", Types.QUOTATION, Flex.QUOTATION)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("/", Types.FORWARD_SLASH_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR)},
            {new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE), new Lex("|", Types.PIPE_MARKER, Flex.ELIXIR), new Lex("body", Types.ELIXIR, Flex.ELIXIR)}
    };
    private static final Lex[] SUFFIX = new Lex[]{
            new Lex("%>", Types.CLOSING, Flex.YYINITIAL)
    };

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        Collection<Object> parameters = new ArrayList<>();

        for (Lex[] firstPrefix : PREFIXES) {
            for (Lex[] secondPrefix : PREFIXES) {
                Lex[] lexes = new Lex[firstPrefix.length + SUFFIX.length + secondPrefix.length + SUFFIX.length];
                System.arraycopy(firstPrefix, 0, lexes, 0, firstPrefix.length);
                System.arraycopy(SUFFIX, 0, lexes, firstPrefix.length, SUFFIX.length);
                System.arraycopy(secondPrefix, 0, lexes, firstPrefix.length + SUFFIX.length, secondPrefix.length);
                System.arraycopy(SUFFIX, 0, lexes, firstPrefix.length + SUFFIX.length + secondPrefix.length, SUFFIX.length);

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
