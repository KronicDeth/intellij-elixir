package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.TokenTypeState;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by luke.imhoff on 9/1/14.
 */
@RunWith(Parameterized.class)
public class ResetTest extends Test {
    private final CharSequence charSequence;
    private final List<TokenTypeState> tokenTypeStates;

    /*
     * Constructors
     */

    public ResetTest(CharSequence charSequence, List<TokenTypeState> tokenTypeStates) {
        this.charSequence = charSequence;
        this.tokenTypeStates = tokenTypeStates;
    }

    /*
     * Methods
     */

    @Parameterized.Parameters(
            name = "\"{0}\" parses and resets stack"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        {
                                "a",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER)
                                )
                        },
                        {
                                "a&&b",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER)
                                )
                        },
                        {
                                "a&&b\nc",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.LAST_EOL),
                                        new TokenTypeState(ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER)
                                )
                        },
                        {
                                "a&&b\nc&&&d",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.LAST_EOL),
                                        new TokenTypeState(ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER),
                                        new TokenTypeState(ElixirTypes.AND_SYMBOL_OPERATOR, ElixirFlexLexer.KEYWORD_PAIR_OR_MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.IDENTIFIER_TOKEN, ElixirFlexLexer.AFTER_UNQUALIFIED_IDENTIFIER)
                                )
                        }
                }
        );
    }

    @org.junit.Test
    public void emptyStack() {
        assertEquals("Stack is not empty after starting: Add `stack.clear();` to `ElixirFlexLexer#reset`, the JFlex generator cannot be customized to do this automatically when regenerating", 0, lexer.stackSize());

        assertParsesCompletely();
        assertParsesCompletely();
    }

    private void assertParsesCompletely() {
        start(charSequence);
        assertEquals("Stack is size 1 after look-ahead on start: Add `stack.clear();` to `ElixirFlexLexer#reset`, the JFlex generator cannot be customized to do this automatically when regenerating", 1, lexer.stackSize());

        for (TokenTypeState tokenTypeState : tokenTypeStates) {
            assertEquals(tokenTypeState.tokenType, lexer.getTokenType());

            lexer.advance();

            assertEquals(tokenTypeState.state, lexer.getState());
        }

        assertNull("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType());

    }
}
