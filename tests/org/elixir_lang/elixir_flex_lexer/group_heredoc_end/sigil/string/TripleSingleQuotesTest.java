package org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.string;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.Elixir;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by luke.imhoff on 9/8/14.
 */
@RunWith(Parameterized.class)
public class TripleSingleQuotesTest extends org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.string.Test {
    /*
     * Constructors
     */

    public TripleSingleQuotesTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
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
                        {"'''", ElixirTypes.HEREDOC_TERMINATOR, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE, true },
                        {"\"\"\"", ElixirTypes.FRAGMENT, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true }
                }
        );
    }

    protected String promoter() {
        return "'''";
    }
}
