package org.elixir_lang.elixir_flex_lexer.group_heredoc_line_start.quote;

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
 * Created by luke.imhoff on 9/3/14.
 */
@RunWith(Parameterized.class)
public class StringTest extends TokenTest {
    /*
     * Constructors
     */

    public StringTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
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
                        {" '''", ElixirTypes.HEREDOC_LINE_WHITE_SPACE_TOKEN, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, false },
                        {" \"\"\"", ElixirTypes.HEREDOC_PREFIX_WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        {"'''", ElixirTypes.FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true },
                        { ";", ElixirTypes.FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true },
                        {"\"\"\"", ElixirTypes.HEREDOC_TERMINATOR, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE, true },
                        {"\f'''", ElixirTypes.HEREDOC_LINE_WHITE_SPACE_TOKEN, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, false },
                        {"\f\"\"\"", ElixirTypes.HEREDOC_PREFIX_WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        {"\n", ElixirTypes.EOL, ElixirFlexLexer.GROUP_HEREDOC_LINE_START, true },
                        { "\r\n", ElixirTypes.EOL, ElixirFlexLexer.GROUP_HEREDOC_LINE_START, true },
                        {"\t'''", ElixirTypes.HEREDOC_LINE_WHITE_SPACE_TOKEN, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, false },
                        {"\t\"\"\"", ElixirTypes.HEREDOC_PREFIX_WHITE_SPACE, ElixirFlexLexer.GROUP_HEREDOC_END, false },
                        {"a", ElixirTypes.FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true }
                }
        );
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger GROUP state
        CharSequence fullCharSequence = "\"\"\"\n" + charSequence;
        super.start(fullCharSequence);
        // consume "\"\"\""
        lexer.advance();
        // consume '\n'
        lexer.advance();
    }
}
