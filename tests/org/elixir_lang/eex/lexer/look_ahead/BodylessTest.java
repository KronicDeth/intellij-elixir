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
public class BodylessTest extends Test {
    private static final Collection<Object[]> PARAMETERS = Arrays.asList(
            new Object[][]{
                    {"<%#", Types.OPENING_COMMENT, Flex.COMMENT},
                    {"<%%", Types.OPENING_QUOTATION, Flex.QUOTATION},
                    {"<%=", Types.OPENING_EQUALS, Flex.ELIXIR}
            }
    );

    private final CharSequence charSequence;
    private final IElementType tokenType;
    private final int bodyState;

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} and advances to state {2}"
    )
    public static Collection<Object[]> parameters() {
        return PARAMETERS;
    }

    public BodylessTest(@NotNull CharSequence charSequence, @NotNull IElementType tokenType, int bodyState) {
        this.charSequence = charSequence;
        this.tokenType = tokenType;
        this.bodyState = bodyState;
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        super.start(charSequence + "%>");
    }

    @org.junit.Test
    public void bodyless() throws IOException {
        start(charSequence);

        assertEquals(tokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(Types.CLOSING, lexer.getTokenType());
        assertEquals(bodyState, lexer.getState());

        lexer.advance();

        assertNull(lexer.getTokenType());
        assertEquals(Flex.YYINITIAL, lexer.getState());
    }
}
