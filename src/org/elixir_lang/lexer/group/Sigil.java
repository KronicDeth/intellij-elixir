package org.elixir_lang.lexer.group;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.lexer.SigilName;
import org.elixir_lang.psi.ElixirTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luke.imhoff on 8/21/14.
 */
public class Sigil extends Base {
    private static final IElementType defaultPromoterType = ElixirTypes.SIGIL_PROMOTER;
    private static final IElementType defaultHeredocPromoterType = ElixirTypes.SIGIL_HEREDOC_PROMOTER;
    private static final IElementType defaultFragmentType = ElixirTypes.SIGIL_FRAGMENT;
    private static final IElementType defaultTerminatorType = ElixirTypes.SIGIL_TERMINATOR;
    private static final IElementType defaultHeredocTerminatorType = ElixirTypes.SIGIL_HEREDOC_TERMINATOR;

    private static Map<Character, Sigil> sigilByLowerCaseSigilName = new HashMap<Character, Sigil>();

    public static final Sigil c = new Sigil(
            'c',
            ElixirTypes.CHAR_LIST_SIGIL_PROMOTER,
            ElixirTypes.CHAR_LIST_SIGIL_HEREDOC_PROMOTER,
            ElixirTypes.CHAR_LIST_FRAGMENT,
            ElixirTypes.CHAR_LIST_SIGIL_TERMINATOR,
            ElixirTypes.CHAR_LIST_SIGIL_HEREDOC_TERMINATOR
    );
    public static final Sigil r = new Sigil(
            'r',
            ElixirTypes.REGEX_PROMOTER,
            ElixirTypes.REGEX_HEREDOC_PROMOTER,
            ElixirTypes.REGEX_FRAGMENT,
            ElixirTypes.REGEX_TERMINATOR,
            ElixirTypes.REGEX_HEREDOC_TERMINATOR
    );
    public static final Sigil s = new Sigil(
            's',
            ElixirTypes.STRING_SIGIL_PROMOTER,
            ElixirTypes.STRING_SIGIL_HEREDOC_PROMOTER,
            ElixirTypes.STRING_FRAGMENT,
            ElixirTypes.STRING_SIGIL_TERMINATOR,
            ElixirTypes.STRING_SIGIL_HEREDOC_TERMINATOR
    );
    public static final Sigil w = new Sigil(
            'w',
            ElixirTypes.WORDS_PROMOTER,
            ElixirTypes.WORDS_HEREDOC_PROMOTER,
            ElixirTypes.WORDS_FRAGMENT,
            ElixirTypes.WORDS_TERMINATOR,
            ElixirTypes.WORDS_HEREDOC_TERMINATOR
    );

    static {
        sigilByLowerCaseSigilName.put(c.lowerCaseSigilName, c);
        sigilByLowerCaseSigilName.put(r.lowerCaseSigilName, r);
        sigilByLowerCaseSigilName.put(s.lowerCaseSigilName, s);
        sigilByLowerCaseSigilName.put(w.lowerCaseSigilName, w);
    }

    public final char lowerCaseSigilName;

    /*
     * Methods
     */

    public static Sigil fetch(char sigilName) {
        if (!SigilName.is(sigilName)) {
           throw new IllegalArgumentException(sigilName + "is not a sigil name");
        }

        char lowerCaseSigilName = Character.toLowerCase(sigilName);

        Sigil sigil = sigilByLowerCaseSigilName.get(lowerCaseSigilName);

        if (sigil == null) {
            /*
             * Create a new SigilGroup and cache it under the assumption that this sigilName will be used more than
             * once if the user took the time to define the sigil_<sigilName> macros.
             */
            sigil = new Sigil(
                    lowerCaseSigilName,
                    defaultPromoterType,
                    defaultHeredocPromoterType,
                    defaultFragmentType,
                    defaultTerminatorType,
                    defaultHeredocTerminatorType
            );

            sigilByLowerCaseSigilName.put(lowerCaseSigilName, sigil);
        }

        return sigil;
    }

    public Sigil(
            char lowerCaseSigilName,
            IElementType promoterType,
            IElementType heredocPromoterType,
            IElementType fragmentType,
            IElementType terminatorType,
            IElementType heredocTerminatorType
    ) {
        super(promoterType, heredocPromoterType, fragmentType, terminatorType, heredocTerminatorType);
        this.lowerCaseSigilName = lowerCaseSigilName;
    }
}
