package org.elixir_lang.structure_view.element;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;

public interface Presentable {
    @NotNull
    ItemPresentation getPresentation();
}
