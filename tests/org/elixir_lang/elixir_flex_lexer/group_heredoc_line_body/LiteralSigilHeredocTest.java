package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_body;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.TokenTest;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests that non-interpolating sigil heredocs ({@code ~S"""}) treat escape sequences and interpolation starts as
 * literal {@code FRAGMENT} tokens instead of entering {@code ESCAPE_SEQUENCE} or {@code INTERPOLATION} states.
 *
 * @see InterpolationTest for the interpolating counterpart
 */
@RunWith(Parameterized.class)
public class LiteralSigilHeredocTest extends TokenTest {

    public LiteralSigilHeredocTest(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    @Parameterized.Parameters(
            name = "\"{0}\" parses as {1} token and advances to state {2}"
    )
    public static Collection<Object[]> generateData() {
        return Arrays.asList(
                new Object[][]{
                        { "#{", ElixirTypes.FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY },
                        { "\\", ElixirTypes.FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY }
                }
        );
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // ~S""" is a non-interpolating sigil heredoc
        CharSequence fullCharSequence = "~S\"\"\"\n" + charSequence;
        super.start(fullCharSequence);
        // consume '~'
        lexer.advance();
        // consume 'S'
        lexer.advance();
        // consume '"""'
        lexer.advance();
        // consume '\n'
        lexer.advance();
    }
}
