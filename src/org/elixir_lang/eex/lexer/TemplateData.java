package org.elixir_lang.eex.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergingLexerAdapterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.OuterLanguageElementType;
import org.elixir_lang.eex.Language;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.eex.psi.Types.DATA;

/**
 * Merges together all EEx opening, body, and closing tokens into a single EEx type
 */
public class TemplateData extends MergingLexerAdapterBase {
    public static final IElementType EEX = new OuterLanguageElementType("EEx", Language.INSTANCE);

    @NotNull
    private final MergeFunction mergeFunction = new MergeFunction();

    public TemplateData(){
        super(new LookAhead());
    }

    @NotNull
    @Override
    public MergeFunction getMergeFunction() {
        return mergeFunction;
    }

    private class MergeFunction implements com.intellij.lexer.MergeFunction {
        @Override
        public IElementType merge(final IElementType type, final Lexer originalLexer) {
            IElementType mergedTokenType;

            if (type != DATA) {
                while (true) {
                    IElementType originalTokenType = originalLexer.getTokenType();

                    if (originalTokenType != null && originalTokenType != DATA) {
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
