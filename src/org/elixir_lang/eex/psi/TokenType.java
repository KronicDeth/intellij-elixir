package org.elixir_lang.eex.psi;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.eex.Language;
import org.jetbrains.annotations.NotNull;

public class TokenType extends IElementType {
    public TokenType(@NotNull String debugName) {
        super(debugName, Language.INSTANCE);
    }
}
