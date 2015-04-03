package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 1/9/15.
 */
public interface Digits extends Quotable {
    public int base();
    public boolean inBase();

    @NotNull
    public IElementType validElementType();
}
