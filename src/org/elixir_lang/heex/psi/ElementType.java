package org.elixir_lang.heex.psi;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.heex.Language;
import org.jetbrains.annotations.NotNull;

public class ElementType extends IElementType {
    public ElementType(@NotNull String debugName) {
        super(debugName, Language.INSTANCE);
    }
}
