package org.elixir_lang.structure_view.element.modular;

import com.intellij.navigation.ItemPresentation;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.KERNEL_MODULE_NAME;

/**
 * A protocol definition
 */
public class Protocol extends Module {
    /*
     * Static Methods
     */

    public static boolean is(Call call) {
        return call.isCallingMacro(KERNEL_MODULE_NAME, "defprotocol", 2);
    }

    /*
     * Constructors
     */

    /**
     *
     * @param call a top-level {@code Kernel.defprotocol/2} call.
     */
    public Protocol(@NotNull Call call) {
        super(call);
    }

    /**
     * @param parent the parent {@link Module} or {@link org.elixir_lang.structure_view.element.Quote} that scopes
     *               {@code call}.
     * @param call   the {@code Kernel.defprotocol/2} call nested in {@code parent}.
     */
    public Protocol(@Nullable Modular parent, @NotNull Call call) {
        super(parent, call);
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.modular.Protocol(location(), navigationItem);
    }
}
