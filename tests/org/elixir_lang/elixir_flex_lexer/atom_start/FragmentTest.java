package org.elixir_lang.elixir_flex_lexer.atom_start;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/28/14.
 */
@RunWith(Parameterized.class)
public class FragmentTest extends TokenTest {
    /*
     * Constructors
     */

    public FragmentTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger ATOM_START state
        CharSequence fullCharSequence = ":" + charSequence;
        super.start(fullCharSequence);
        // consume ':'
        lexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                        { ";", TokenType.BAD_CHARACTER, ElixirFlexLexer.ATOM_START },
                        { "A", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\n", ElixirTypes.EOL, ElixirFlexLexer.SIGN_OPERATION_MAYBE },
                        { "\r\n", ElixirTypes.EOL, ElixirFlexLexer.SIGN_OPERATION_MAYBE },
                        { "_!", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "_", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "_0", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "_?", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "_@", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "_A", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "__", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "_a", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "a", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "after", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "afterwards", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "and", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "androids", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "catch", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "catchall", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "do", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "done", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "else", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "elsewhere", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "end", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "ending", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "false", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "falsehood", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "fn", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "fnctn", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "in", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "inner", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "nil", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "nils", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "not", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "notifiers", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "or", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "order", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "rescue", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "rescuer", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "true", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "truest", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "when", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL },
                        { "whenever", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.YYINITIAL }
                }
        );
    }
}
