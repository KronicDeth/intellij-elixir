package org.elixir_lang.eex.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergingLexerAdapterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.eex.psi.TokenType;
import org.elixir_lang.eex.psi.Types;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.elixir_lang.eex.psi.Types.DATA;
import static org.elixir_lang.eex.psi.Types.ELIXIR;

/**
 * Merges together all EEx opening, body, and closing tokens into a single EEx type
 */
public class TemplateData extends MergingLexerAdapterBase {
    public static final TokenSet NOT_DATA;
    public static final TokenSet NOT_ELIXIR;
    private static final TokenSet ALL;
    public static final IElementType EEX = new org.elixir_lang.eex.psi.TokenType("EEx");

    static {
        ALL = TokenSet.create(
                Arrays
                        .stream(Types.class.getDeclaredFields())
                        .map(field -> {
                            Object value;

                            try {
                                value = field.get(null);
                            } catch (IllegalAccessException e) {
                                value = null;
                            }

                            return value;
                        })
                        .filter(TokenType.class::isInstance)
                        .map(TokenType.class::cast)
                        .toArray(IElementType[]::new)
        );
        NOT_DATA = TokenSet.andNot(ALL, TokenSet.create(DATA));
        NOT_ELIXIR = TokenSet.andNot(ALL, TokenSet.create(ELIXIR));
    }

    @NotNull
    private final TokenSet tokenSet;
    private final MergeFunction mergeFunction = new MergeFunction();

    public TemplateData(@NotNull final TokenSet merge){
        super(new LookAhead());
        tokenSet = merge;
    }

    @Override
    public MergeFunction getMergeFunction() {
        return mergeFunction;
    }

    private class MergeFunction implements com.intellij.lexer.MergeFunction {
        @Override
        public IElementType merge(final IElementType type, final Lexer originalLexer) {
            IElementType mergedTokenType;

            if (tokenSet.contains(type)) {
                while (true) {
                    IElementType originalTokenType = originalLexer.getTokenType();

                    if (tokenSet.contains(originalTokenType)) {
                        originalLexer.advance();
                    } else {
                        break;
                    }
                }

                mergedTokenType = EEX;
            } else {
                mergedTokenType = type;
            }

            return mergedTokenType;
        }
    }
}
