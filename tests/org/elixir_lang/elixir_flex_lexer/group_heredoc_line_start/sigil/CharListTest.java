package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_start.sigil;

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
 * Created by luke.imhoff on 9/3/14.
 */
@RunWith(Parameterized.class)
public class CharListTest extends TokenTest {
    /*
     * Constructors
     */

    public CharListTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
        super(charSequence, tokenType, lexicalState, consumeAll);
    }

    /*
     * Methods
     */

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(
                new Object[][]{
                        { " '''", TokenType.WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        { " \"\"\"", TokenType.WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        { "'''", ElixirTypes.CHAR_LIST_SIGIL_HEREDOC_TERMINATOR, ElixirFlexLexer.SIGIL_MODIFIERS, true },
                        { "\"\"\"", ElixirTypes.CHAR_LIST_FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, false },
                        { "\f'''", TokenType.WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        { "\f\"\"\"", TokenType.WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        { "\n", ElixirTypes.CHAR_LIST_FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_START, true },
                        { "\t'''", TokenType.WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        { "\t\"\"\"", TokenType.WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        { "a", ElixirTypes.CHAR_LIST_FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true }
                }
        );
    }

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger GROUP state
        CharSequence fullCharSequence = "~c'''\n" + charSequence;
        super.reset(fullCharSequence);
        // consume '~'
        flexLexer.advance();
        // consume 'c'
        flexLexer.advance();
        // consume "'''"
        flexLexer.advance();
        // consume '\n'
        flexLexer.advance();
    }
}
