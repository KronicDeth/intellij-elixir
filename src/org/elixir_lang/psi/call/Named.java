package org.elixir_lang.psi.call;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.NamedElement;
import org.jetbrains.annotations.NotNull;

public interface Named extends Call, NamedElement {
    /**
     * The element holding not the name of the function being called, but the structure defined by this call, so for
     * {@code Elixir.Kernel.def}, this would be the function name and for {@code Elixir.Kernel.defmodule}, it would
     * be the module name.
     *
     * To get the name identifier without checking if a known Kernel macro, use {@link Call#functionNameElement()}.
     */
    @NotNull
    PsiElement getNameIdentifier();
}
