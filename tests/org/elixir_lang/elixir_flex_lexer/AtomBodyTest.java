package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/16/14.
 */
@RunWith(Parameterized.class)
public class AtomBodyTest extends TokenTest {
    /*
     * Constructors
     */

    public AtomBodyTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger ATOM_BODY state
        CharSequence fullCharSequence = ":_" + charSequence;
        super.reset(fullCharSequence);
        // consume ':'
        flexLexer.advance();
        // consume '_'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
                        { " ", TokenType.WHITE_SPACE, INITIAL_STATE },
                        { "!", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "0", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY },
                        { "?", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "@", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY },
                        { "A", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY },
                        { "\n", ElixirTypes.EOL, INITIAL_STATE },
                        { "_", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY },
                        { "a", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ATOM_BODY }
                }
        );
    }
}
