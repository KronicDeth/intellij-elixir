package org.elixir_lang;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.psi.ElixirTypes;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class ElixirLexer extends LookAheadLexer {
    public static final TokenSet FRAGMENTS = TokenSet.create(
            ElixirTypes.CHAR_LIST_FRAGMENT,
            ElixirTypes.SIGIL_FRAGMENT,
            ElixirTypes.STRING_FRAGMENT
    );

    public ElixirLexer() {
        super(
                new MergingLexerAdapter(
                        new ElixirFlexLexerAdapter(),
                        FRAGMENTS
                )
        );
    }
}
