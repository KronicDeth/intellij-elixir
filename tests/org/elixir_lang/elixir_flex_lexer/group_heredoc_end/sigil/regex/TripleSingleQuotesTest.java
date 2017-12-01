package org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.regex;

import com.intellij.psi.tree.IElementType;
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
public class TripleSingleQuotesTest extends Test {
    /*
     * Constants
     */

    public static final IElementType FRAGMENT_TYPE = ElixirTypes.CHAR_LIST_FRAGMENT;
    public static final IElementType TERMINATOR_TYPE = ElixirTypes.CHAR_LIST_HEREDOC_TERMINATOR;

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
                        {"'''", TERMINATOR_TYPE, ElixirFlexLexer.YYINITIAL, true },
                        {"\"\"\"", FRAGMENT_TYPE, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true }
                }
        );
    }

    protected String promoter() {
        return "'''";
    }
}
