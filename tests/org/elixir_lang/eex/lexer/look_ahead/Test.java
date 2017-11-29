package org.elixir_lang.eex.lexer.look_ahead;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.eex.lexer.LookAhead;
import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.TokenType;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@Ignore("abstract")
public class Test extends org.elixir_lang.flex_lexer.Test<LookAhead> {
    public static class Lex {
        @NotNull
        final CharSequence text;
        @NotNull
        public final IElementType tokenType;
        public final int state;

        Lex(@NotNull CharSequence text, @NotNull IElementType tokenType, int state) {
            this.text = text;
            this.tokenType = tokenType;
            this.state = state;
        }

        @NotNull
        CharSequence text() {
            return text;
        }

        @Override
        public String toString() {
            return "\"" + text + "\" parses as " + tokenType + " and advances to state " + state;
        }
    }

    public static class Sequence {
        @NotNull
        final Lex[] lexes;
        @NotNull
        final CharSequence text;

        private static String text(@NotNull Lex... lexes) {
            return Arrays.stream(lexes).map(Lex::text).collect(Collectors.joining());
        }

        Sequence(@NotNull Lex... lexes) {
            this.lexes = lexes;
            this.text = text(lexes);
        }

        public String toString() {
            return "\"" + text + "\" is a sequence where " +
                    Arrays.stream(lexes).map(Lex::toString).collect(Collectors.joining("; "));
        }
    }

    /*
     * Fields
     */

    @NotNull
    public final Sequence sequence;

    /*
     * Constructors
     */

    public Test(@NotNull Sequence sequence) {
        this.sequence = sequence;
    }

    /*
     * Methods
     */

    @Override
    protected LookAhead lexer() {
        return new LookAhead();
    }

    protected void assertSequence() {
                start(sequence.text);
        Lex[] lexes = sequence.lexes;

        for (int i = 0; i < lexes.length; i++) {
            Lex lex = lexes[i];

            // text and tokenType before advancing to state because lexer is look-ahead
            assertEquals("Text #" + i + " expected (\"" + lex.text + "\") differs from actual (\"" + lexer.getTokenText() +  "\")", lex.text, lexer.getTokenText());
            assertEquals("TokenType " + i, lex.tokenType, lexer.getTokenType());

            lexer.advance();

            assertEquals("State " + i, lex.state, lexer.getState());
        }

    }
}
