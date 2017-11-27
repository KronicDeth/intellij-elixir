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
        public final CharSequence text;
        @NotNull
        public final IElementType tokenType;
        public final int state;

        public Lex(@NotNull CharSequence text, @NotNull IElementType tokenType, int state) {
            this.text = text;
            this.tokenType = tokenType;
            this.state = state;
        }

        @NotNull
        public CharSequence text() {
            return text;
        }

        @Override
        public String toString() {
            return "\"" + text + "\" parses as " + tokenType + " and advances to state " + state;
        }
    }

    public static class Sequence {
        @NotNull
        public final Lex[] lexes;
        @NotNull
        public final CharSequence text;

        private static String text(@NotNull Lex... lexes) {
            return Arrays.stream(lexes).map(Lex::text).collect(Collectors.joining());
        }

        public Sequence(@NotNull Lex... lexes) {
            this.lexes = lexes;
            this.text = text(lexes);
        }

        public String toString() {
            return "\"" + text + "\" is a sequence where " +
                    Arrays.stream(lexes).map(Lex::toString).collect(Collectors.joining("; "));
        }
    }

    /*
     * Constants
     */

    public static final int INITIAL_STATE = Flex.YYINITIAL;

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
    protected int initialState() {
        return INITIAL_STATE;
    }

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
