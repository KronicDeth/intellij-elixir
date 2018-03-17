package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

public interface Digits extends Quotable {
    int base();
    boolean inBase();

    @Nullable
    IElementType validElementType();
}
