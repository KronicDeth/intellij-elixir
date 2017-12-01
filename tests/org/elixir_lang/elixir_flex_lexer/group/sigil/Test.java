package org.elixir_lang.elixir_flex_lexer.group.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by luke.imhoff on 9/6/14.
 */
@Ignore("abstract")
public abstract class Test extends org.elixir_lang.elixir_flex_lexer.group.Test {
    /*
     * CONSTANTS
     */

    public static final IElementType FRAGMENT_TYPE = ElixirTypes.SIGIL_FRAGMENT;
    public static final int LEXICAL_STATE = ElixirFlexLexer.GROUP;
    public static final Collection<Object[]> QUOTE_DATA = Arrays.asList(
            new Object[][]{
                    {"'", FRAGMENT_TYPE, LEXICAL_STATE},
                    {"\"", FRAGMENT_TYPE, LEXICAL_STATE}
            }
    );

    /*
     * Constructors
     */

    public Test(CharSequence charSequence, IElementType tokenType, int lexicalState) {
        super(charSequence, tokenType, lexicalState);
    }

    /*
     * Methods
     */

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start of "~x" + promoter to trigger GROUP state
        CharSequence fullCharSequence = "~x" + promoter() + charSequence;
        // consume ~
        super.start(fullCharSequence);
        // consume x
        lexer.advance();
        // consume promoter
        lexer.advance();
    }

    protected abstract char promoter();

    public static Collection<Object[]> generateData(Collection<Object[]> terminatorData) {
        Collection<Object[]> combinedData = new Vector<Object[]>();
        combinedData.addAll(QUOTE_DATA);
        combinedData.addAll(terminatorData);

        return combinedData;
    }
}
