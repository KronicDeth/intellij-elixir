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
        // consume ':'
        flexLexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                        { ";", TokenType.BAD_CHARACTER, ElixirFlexLexer.ATOM_START },
                        { "A", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\n", ElixirTypes.EOL, INITIAL_STATE },
                        { "\r\n", ElixirTypes.EOL, INITIAL_STATE },
                        { "_!", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "_", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "_0", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "_?", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "_@", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "_A", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "__", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "_a", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "a", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "after", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "afterwards", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "and", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "androids", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "catch", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "catchall", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "do", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "done", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "else", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "elsewhere", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "end", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "ending", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "false", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "falsehood", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "fn", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "fnctn", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "in", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "inner", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "nil", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "nils", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "not", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "notifiers", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "or", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "order", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "rescue", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "rescuer", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "true", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "truest", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "when", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE },
                        { "whenever", ElixirTypes.ATOM_FRAGMENT, INITIAL_STATE }
                }
        );
    }
}
