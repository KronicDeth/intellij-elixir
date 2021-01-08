package org.elixir_lang.elixir_flex_lexer.group_heredoc_end.sigil.custom;

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
public class TripleDoubleQuotesTest extends Test {
    /*
     * Constants
     */

    public static final IElementType FRAGMENT_TYPE = ElixirTypes.STRING_FRAGMENT;
    public static final IElementType TERMINATOR_TYPE = ElixirTypes.STRING_HEREDOC_TERMINATOR;

    /*
     * Constructors
     */

    public TripleDoubleQuotesTest(CharSequence charSequence, IElementType tokenType, int lexicalState, boolean consumeAll) {
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
                        {"'''", FRAGMENT_TYPE, ElixirFlexLexer.GROUP_HEREDOC_LINE_BODY, true},
                        {"\"\"\"", TERMINATOR_TYPE, ElixirFlexLexer.ADDITION_OR_SUBTRACTION_MAYBE, true}
                }
        );
    }

    protected String promoter() {
        return "\"\"\"";
    }
}
