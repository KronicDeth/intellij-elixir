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
            name = "{index} \"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][] {
                        { "'", ElixirTypes.CHAR_LIST_PROMOTER, ElixirFlexLexer.GROUP },
                        { ";", ElixirTypes.SEMICOLON, ElixirFlexLexer.MULTILINE_WHITE_SPACE_MAYBE },
                        { "A", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "\"", ElixirTypes.STRING_PROMOTER, ElixirFlexLexer.GROUP },
                        { "\n", ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL },
                        { "\r\n", ElixirTypes.EOL, ElixirFlexLexer.YYINITIAL },
                        { "_!", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "_", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "_0", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "_?", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "_@", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "_A", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "__", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "_a", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "a", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "after", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "afterwards", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "and", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "androids", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "catch", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "catchall", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "do", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "done", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "else", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "elsewhere", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "end", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "ending", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "false", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "falsehood", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "fn", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "fnctn", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "in", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "inner", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "nil", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "nils", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "not", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "notifiers", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "or", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "order", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "rescue", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "rescuer", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "true", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "truest", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "when", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE },
                        { "whenever", ElixirTypes.ATOM_FRAGMENT, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_OR_WHITE_SPACE_MAYBE }
                }
        );
    }
}
