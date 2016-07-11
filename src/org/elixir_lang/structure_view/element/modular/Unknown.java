package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Unknown extends Module {
    /*
     * Static Methods
     */

    public static boolean is(Call call) {
        return call.hasDoBlockOrKeyword();
    }

    /*
     * Constructors
     */

    public Unknown(@NotNull Call call) {
        super(call);
    }

    /**
     * @param parent the parent {@link Module} or {@link org.elixir_lang.structure_view.element.Quote} that scopes
     *               {@code call}.
     * @param call   the {@code <module>.def<suffix>/2} call nested in {@code parent}.
     */
    public Unknown(@Nullable Modular parent, @NotNull Call call) {
        super(parent, call);
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.modular.Unknown(location(), navigationItem);
    }
}
