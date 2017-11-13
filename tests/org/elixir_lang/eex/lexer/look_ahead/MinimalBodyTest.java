package org.elixir_lang.eex.lexer.look_ahead;

import com.intellij.psi.tree.IElementType;
import org.apache.commons.lang.ArrayUtils;
import org.elixir_lang.eex.lexer.Flex;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class MinimalBodyTest extends Test {
    private static final Collection<Object[]> PARAMETERS = Arrays.asList(
            new Object[][]{
                    {"<%", Types.OPENING, Flex.ELIXIR, Types.ELIXIR},
                    {"<%#", Types.OPENING_COMMENT, Flex.COMMENT, Types.COMMENT},
                    {"<%%", Types.OPENING_QUOTATION, Flex.QUOTATION, Types.QUOTATION},
                    {"<%=", Types.OPENING_EQUALS, Flex.ELIXIR, Types.ELIXIR}
            }
    );

    private final CharSequence firstOpening;
    private final IElementType firstOpeningTokenType;
    private final int firstBodyState;
    private final IElementType firstBodyTokenType;
    private final CharSequence secondOpening;
    private final IElementType secondOpeningTokenType;
    private final int secondBodyState;
    private final IElementType secondBodyTokenType;

    @Parameterized.Parameters(
            name = "\"{0} %>{4} %>\": parses {1} and advances to state {2}; parses {3} and advances to state {2}; parses {5} and advances to state {6}; parses {7} and advances to {6}"
    )
    public static Collection<Object[]> parameters() {
        Collection<Object[]> parameters = new ArrayList<>();

        for (Object[] first : PARAMETERS) {
            for (Object[] second : PARAMETERS) {
                parameters.add(ArrayUtils.addAll(first, second));
            }
        }

        return parameters;
    }

    public MinimalBodyTest(@NotNull CharSequence firstOpening, @NotNull IElementType firstOpeningTokenType, int firstBodyState, @NotNull IElementType firstBodyTokenType,
                           @NotNull CharSequence secondOpening, @NotNull IElementType secondOpeningTokenType, int secondBodyState, @NotNull IElementType secondBodyTokenType) {
        this.firstOpening = firstOpening;
        this.firstOpeningTokenType = firstOpeningTokenType;
        this.firstBodyState = firstBodyState;
        this.firstBodyTokenType = firstBodyTokenType;
        this.secondOpening = secondOpening;
        this.secondOpeningTokenType = secondOpeningTokenType;
        this.secondBodyState = secondBodyState;
        this.secondBodyTokenType = secondBodyTokenType;
    }

    @org.junit.Test
    public void minimalBody() throws IOException {
        start(firstOpening + " first body %>" + secondOpening + " second body %>");

        assertEquals(firstOpeningTokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(firstBodyState, lexer.getState());
        assertEquals(" first body ", lexer.getTokenText());
        assertEquals(firstBodyTokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(firstBodyState, lexer.getState());
        assertEquals(Types.CLOSING, lexer.getTokenType());

        lexer.advance();

        assertEquals(Flex.YYINITIAL, lexer.getState());
        assertEquals(secondOpeningTokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(secondBodyState, lexer.getState());
        assertEquals(" second body ", lexer.getTokenText());
        assertEquals(secondBodyTokenType, lexer.getTokenType());

        lexer.advance();

        assertEquals(secondBodyState, lexer.getState());
        assertEquals(Types.CLOSING, lexer.getTokenType());

        lexer.advance();

        assertEquals(Flex.YYINITIAL, lexer.getState());
        assertNull(lexer.getTokenType());
    }
}
