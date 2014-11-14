package org.elixir_lang.elixir_flex_lexer.atom_start;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.elixir_lang.psi.ElixirTypes;
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
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger ATOM_START state
        CharSequence fullCharSequence = ":" + charSequence;
        super.reset(fullCharSequence);
        // consume '~'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                        { ";", TokenType.BAD_CHARACTER, ElixirFlexLexer.ATOM_START },
                        { "A", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\n", ElixirTypes.EOL, INITIAL_STATE },
                        { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                        { "_", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY },
                        { "a", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY }
                }
        );
    }
}
