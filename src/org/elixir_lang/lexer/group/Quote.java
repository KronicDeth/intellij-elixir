package org.elixir_lang.lexer.group;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luke.imhoff on 8/21/14.
 */
public class Quote extends Base {
    private static Map<String, Quote> quoteByPromoter = new HashMap<String, Quote>();
    private static Map<String, Quote> quoteByTerminator = new HashMap<String, Quote>();

    public static final String charListPromoter = "'";
    public static final String charListHeredocPromoter = "'''";
    public static final String stringPromoter = "\"";
    public static final String stringHeredocPromoter = "\"\"\"";

    public static final String charListTerminator = "'";
    public static final String charListHeredocTerminator = "'''";
    public static final String stringTerminator = "\"";
    public static final String stringHeredocTerminator = "\"\"\"";

    public static final Quote charList = new Quote(
            ElixirTypes.CHAR_LIST_PROMOTER,
            ElixirTypes.CHAR_LIST_HEREDOC_PROMOTER,
            ElixirTypes.CHAR_LIST_FRAGMENT,
            ElixirTypes.CHAR_LIST_TERMINATOR,
            ElixirTypes.CHAR_LIST_HEREDOC_TERMINATOR
    );
    public static final Quote string = new Quote(
            ElixirTypes.STRING_PROMOTER,
            ElixirTypes.STRING_HEREDOC_PROMOTER,
            ElixirTypes.STRING_FRAGMENT,
            ElixirTypes.STRING_TERMINATOR,
            ElixirTypes.STRING_HEREDOC_TERMINATOR
    );

    static {
        quoteByPromoter.put(charListPromoter, charList);
        quoteByPromoter.put(charListHeredocPromoter, charList);
        quoteByPromoter.put(stringPromoter, string);
        quoteByPromoter.put(stringHeredocPromoter, string);

        quoteByTerminator.put(charListTerminator, charList);
        quoteByTerminator.put(charListHeredocTerminator, charList);
        quoteByTerminator.put(stringTerminator, string);
        quoteByTerminator.put(stringHeredocTerminator, string);
    }

    @NotNull
    public static Quote fetch(String promoter) {
        Quote quote =  quoteByPromoter.get(promoter);

        if (quote == null) {
            throw new IllegalArgumentException("No Quote promoted by " + promoter);
        }

        return quote;
    }

    public Quote(IElementType promoterType, IElementType heredocPromoterType, IElementType fragmentType, IElementType terminatorType, IElementType heredocTerminatorType) {
        super(promoterType, heredocPromoterType, fragmentType, terminatorType, heredocTerminatorType);
    }
}
