package org.elixir_lang.beam.psi;

import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class CallDefinitionElement extends CompositeElement {
    public CallDefinitionElement(@NotNull IElementType type) {
        super(type);
    }
}
