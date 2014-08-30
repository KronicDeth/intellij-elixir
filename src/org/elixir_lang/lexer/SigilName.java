package org.elixir_lang.lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luke.imhoff on 8/20/14.
 */
public class SigilName {
    private static final Map<Character, IElementType> elementTypeBySigilName = new HashMap<Character, IElementType>();

    static {
        elementTypeBySigilName.put('R', ElixirTypes.LITERAL_REGEX_SIGIL_NAME);
        elementTypeBySigilName.put('S', ElixirTypes.LITERAL_STRING_SIGIL_NAME);
        elementTypeBySigilName.put('W', ElixirTypes.LITERAL_WORDS_SIGIL_NAME);
        elementTypeBySigilName.put('r', ElixirTypes.INTERPOLATING_REGEX_SIGIL_NAME);
        elementTypeBySigilName.put('s', ElixirTypes.INTERPOLATING_STRING_SIGIL_NAME);
        elementTypeBySigilName.put('w', ElixirTypes.INTERPOLATING_WORDS_SIGIL_NAME);
    }

    /*
     * Methods
     */

    public static IElementType elementType(char sigilName) {
        IElementType elementType = elementTypeBySigilName.get(sigilName);

        if (elementType == null) {
            if (isInterpolating(sigilName)) {
                elementType = ElixirTypes.INTERPOLATING_SIGIL_NAME;
            } else {
                elementType = ElixirTypes.LITERAL_SIGIL_NAME;
            }

            elementTypeBySigilName.put(sigilName, elementType);
        }

        return elementType;
    }

    public static boolean is(char character) {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }

    public static boolean isInterpolating(char sigilName) {
        return (sigilName >= 'a' && sigilName <= 'z');
    }
}
