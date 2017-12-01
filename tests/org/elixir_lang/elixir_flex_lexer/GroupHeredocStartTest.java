package org.elixir_lang.elixir_flex_lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/4/14.
 */
@RunWith(Parameterized.class)
public class GroupHeredocStartTest extends TokenTest {
    /*
     * Constructors
     */

    public GroupHeredocStartTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger GROUP_HEREDOC_START state
        CharSequence fullCharSequence = "'''" + charSequence;
        super.start(fullCharSequence);
        // consume "'''"
        lexer.advance();
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(new Object[][]{
                        {";", TokenType.BAD_CHARACTER, ElixirFlexLexer.GROUP_HEREDOC_START },
                        {"\n", ElixirTypes.EOL, ElixirFlexLexer.GROUP_HEREDOC_LINE_START },
                        {"\r\n", ElixirTypes.EOL, ElixirFlexLexer.GROUP_HEREDOC_LINE_START },
                        {"a", TokenType.BAD_CHARACTER, ElixirFlexLexer.GROUP_HEREDOC_START }
                }
        );
    }
}
