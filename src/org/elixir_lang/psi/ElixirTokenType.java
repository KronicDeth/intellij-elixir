package org.elixir_lang.psi;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 7/28/14.
 */
public class ElixirTokenType extends IElementType {
    public ElixirTokenType(@NotNull @NonNls String debugName) {
        super(debugName, ElixirLanguage.INSTANCE);
    }
}

