package org.elixir_lang.lexer;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luke.imhoff on 8/20/14.
 */
public class SigilName {
    public static IElementType elementType(char sigilName) {
        IElementType elementType;

        if (isInterpolating(sigilName)) {
            elementType = ElixirTypes.INTERPOLATING_SIGIL_NAME;
        } else {
            elementType = ElixirTypes.LITERAL_SIGIL_NAME;
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
