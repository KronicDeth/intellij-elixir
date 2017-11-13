package org.elixir_lang.eex.lexer.look_ahead;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class WhiteSpaceBodyTest extends Test {
    private static final Collection<Object[]> PARAMETERS = Arrays.asList(
            new Object[][]{
                    {"<%", Types.OPENING, Flex.ELIXIR, Types.ELIXIR},
                    {"<%#", Types.OPENING_COMMENT, Flex.COMMENT, Types.COMMENT},
                    {"<%%", Types.OPENING_QUOTATION, Flex.QUOTATION, Types.QUOTATION},
                    {"<%=", Types.OPENING_EQUALS, Flex.ELIXIR, Types.ELIXIR}
            }
    );

    private final CharSequence charSequence;
    private final IElementType openingTokenType;
    private final int bodyState;
    private final IElementType bodyTokenType;

    @Parameterized.Parameters(
            name = "\"{0} %>\": parses {1} and advances to state {2}; parses {3} and advances to state {2}"
    )
    public static Collection<Object[]> parameters() {
        return PARAMETERS;
    }

    public WhiteSpaceBodyTest(@NotNull CharSequence charSequence, @NotNull IElementType openingTokenType, int bodyState, @NotNull IElementType bodyTokenType) {
        this.charSequence = charSequence;
        this.openingTokenType = openingTokenType;
        this.bodyState = bodyState;
        this.bodyTokenType = bodyTokenType;
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        CharSequence fullCharSequence = charSequence + " %>";
        super.start(fullCharSequence);
    }

    @org.junit.Test
    public void whiteSpaceBody() {
        start(charSequence);

        assertEquals(openingTokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(bodyState, lexer.getState());
        assertEquals(bodyTokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(bodyState, lexer.getState());
        assertEquals(Types.CLOSING, lexer.getTokenType());

        lexer.advance();

        assertNull(lexer.getTokenType());
        assertEquals(Flex.YYINITIAL, lexer.getState());
    }
}
