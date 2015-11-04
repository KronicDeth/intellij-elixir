package org.elixir_lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class ElementTypeFactory {
    /*
     * Static Methods
     */

    public static IElementType factory(@NotNull String name) {
        if (name.equals("ALIAS")) {
            return new org.elixir_lang.psi.stub.type.Alias(name);
        } else if (name.equals("MATCHED_QUALIFIED_ALIAS")) {
            return new org.elixir_lang.psi.stub.type.MatchedQualifiedAlias(name);
        }

        throw new RuntimeException("Unknown element type: " + name);
    }

    /*
     * Constructors
     */

    private ElementTypeFactory() {
    }
}
