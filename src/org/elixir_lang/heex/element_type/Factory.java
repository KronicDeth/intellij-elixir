package org.elixir_lang.heex.element_type;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElementTypeFactory;
import org.jetbrains.annotations.NotNull;

public class Factory {
    @NotNull
    public static IElementType factory(@NotNull String name) {
        return ElementTypeFactory.factory("org.elixir_lang.heex.psi.stub.type", name);
    }
}
