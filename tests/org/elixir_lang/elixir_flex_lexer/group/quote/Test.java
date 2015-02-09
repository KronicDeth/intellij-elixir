package org.elixir_lang.elixir_flex_lexer.group.quote;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by luke.imhoff on 9/6/14.
 */
@Ignore("abstract")
public abstract class Test extends org.elixir_lang.elixir_flex_lexer.group.Test {
    /*
     * Constants
     */

    public static final int LEXICAL_STATE = ElixirFlexLexer.GROUP;

    /*
     * Constructors
     */


    public Test(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    public static Collection<Object[]> generateData(IElementType fragmentType, Collection<Object[]> quoteData) {
        Collection<Object[]> commonData = Arrays.asList(new Object[][]{
                        { ")", fragmentType, LEXICAL_STATE },
                        { "/", fragmentType, LEXICAL_STATE },
                        { ";", fragmentType, LEXICAL_STATE },
                        { ">", fragmentType, LEXICAL_STATE },
                        { "\n", fragmentType, LEXICAL_STATE },
                        { "\r\n", fragmentType, LEXICAL_STATE },
                        { "]", fragmentType, LEXICAL_STATE },
                        { "a", fragmentType, LEXICAL_STATE },
                        { "|", fragmentType, LEXICAL_STATE },
                        { "}", fragmentType, LEXICAL_STATE }
                }
        );

        Collection<Object[]> combinedData = new Vector<Object[]>();
        combinedData.addAll(commonData);
        combinedData.addAll(quoteData);

        return combinedData;
    }
}
