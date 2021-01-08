package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.TokenTypeState;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 12/7/14.
 */
@RunWith(Parameterized.class)
public class KeywordPairTest extends Test {
    /*
     * Fields
     */

    private CharSequence identifierCharSequence;
    private List<TokenTypeState> tokenTypeStates;

    /*
     * Constructors
     */

    public KeywordPairTest(CharSequence identifierCharSequence, List<TokenTypeState> tokenTypeStates) {
        this.identifierCharSequence = identifierCharSequence;
        this.tokenTypeStates = tokenTypeStates;
    }

    /*
     * Static Methods
     */

    @Parameterized.Parameters(
            name = "\"{0}\" parses"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        {
                                "\"key\": 1",
                                Arrays.asList(
                                        new TokenTypeState(ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_FRAGMENT, ElixirFlexLexer.GROUP),
                                        new TokenTypeState(ElixirTypes.STRING_TERMINATOR, ElixirFlexLexer.ADDITION_OR_KEYWORD_PAIR_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(ElixirTypes.KEYWORD_PAIR_COLON, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE),
                                        new TokenTypeState(TokenType.WHITE_SPACE, ElixirFlexLexer.YYINITIAL),
                                        new TokenTypeState(ElixirTypes.VALID_DECIMAL_DIGITS, ElixirFlexLexer.DECIMAL_WHOLE_NUMBER)
        )
                        }
                }
        );
    }

    /*
     * Instance Methods
     */

    @org.junit.Test
    public void keywordKey() {
        start(identifierCharSequence);

        for (TokenTypeState tokenTypeState: tokenTypeStates) {
            assertEquals(tokenTypeState.tokenType, lexer.getTokenType());

            lexer.advance();

            assertEquals(tokenTypeState.state, lexer.getState());
        }

        end();
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        CharSequence fullCharSequence = "[" + charSequence + "]";
        super.start(fullCharSequence);

        assertEquals(ElixirTypes.OPENING_BRACKET, lexer.getTokenType());

        lexer.advance();

        assertEquals(ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE, lexer.getState());
    }

    protected void end() {
        assertEquals(ElixirTypes.CLOSING_BRACKET, lexer.getTokenType());

        lexer.advance();

        assertEquals(ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE, lexer.getState());
    }
}
