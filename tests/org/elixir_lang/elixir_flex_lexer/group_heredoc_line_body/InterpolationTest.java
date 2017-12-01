package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body;

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
 * Created by luke.imhoff on 9/2/14.
 */
@RunWith(Parameterized.class)
public class InterpolationTest extends TokenTest {
    /*
     * Constructors
     */

    public InterpolationTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
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
                        { "#{", ElixirTypes.INTERPOLATION_START, ElixirFlexLexer.INTERPOLATION },
                        { "\\", ElixirTypes.ESCAPE, ElixirFlexLexer.ESCAPE_SEQUENCE }
                }
        );
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger GROUP state with isInterpolating being true
        CharSequence fullCharSequence = "\"\"\"\n" + charSequence;
        super.start(fullCharSequence);
        // consume "\"\"\""
        lexer.advance();
        // consume '\n'
        lexer.advance();
    }
}
