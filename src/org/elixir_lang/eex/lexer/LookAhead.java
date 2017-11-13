package org.elixir_lang.eex.lexer;

import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.eex.psi.Types;

public class LookAhead extends com.intellij.lexer.LookAheadLexer {
    public static final TokenSet MERGABLE_TOKEN_SET = TokenSet.create(
            Types.COMMENT,
            Types.DATA,
            Types.ELIXIR,
            Types.QUOTATION
    );

    public LookAhead() {
        super(
                new MergingLexerAdapter(
                        new Adapter(),
                        MERGABLE_TOKEN_SET
                )
        );
    }
}
