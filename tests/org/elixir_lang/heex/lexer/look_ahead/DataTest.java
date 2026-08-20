package org.elixir_lang.heex.lexer.look_ahead;

import org.elixir_lang.heex.lexer.Flex;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * Plain data that HEEx.flex does not treat specially at all: doctype and multi-line text merge
 * into a single DATA token with no state changes, matching
 * phoenix_live_view/test/phoenix_live_view/tag_engine/tokenizer_test.exs's "text" and "doctype"
 * describe blocks (the tag-name/attribute structure those tests assert on comes from the
 * downstream HTML lexer, not this one - HEEx.flex only separates DATA from EEx tags and braces).
 */
@RunWith(Parameterized.class)
public class DataTest extends Test {
    private static final Lex[][] SEQUENCES = new Lex[][]{
            {new Lex("<!doctype html>", Types.DATA, Flex.YYINITIAL)},
            {new Lex("<!DOCTYPE\nhtml\n>", Types.DATA, Flex.YYINITIAL)},
            {new Lex("first\nsecond\nthird\n", Types.DATA, Flex.YYINITIAL)},
            {new Lex("first\nsecond\r\nthird", Types.DATA, Flex.YYINITIAL)}
    };

    public DataTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        return Arrays.stream(SEQUENCES).<Object>map(Sequence::new)::iterator;
    }

    @org.junit.Test
    public void data() {
        assertSequence();
    }
}
