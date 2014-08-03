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
    public static final TokenSet NON_CODE = TokenSet.create(ElixirTypes.COMMENT, TokenType.WHITE_SPACE);

    public ElixirLexer() {
        super(
                /* The 'Merging' in the name is a little confusing.  The second argument, `tokensToMerge`, aren't merged
                   into the stream, they are skipped over and actually removed from the stream, so by including COMMENTs
                   and WHITE_SPACE in the `tokensToMerge`, they will removed from the stream and don't need to be
                   processed in Elixir.bnf.
                 */
                new MergingLexerAdapter(
                        new ElixirFlexLexerAdapter(),
                        // tokens to skip over
                        NON_CODE
                )
        );
    }
}
