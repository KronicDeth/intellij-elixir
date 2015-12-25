package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;

/**
 * A {@code PsiElement} that has arguments for a {@link org.elixir_lang.psi.call.Call}
 */
public interface Arguments extends PsiElement {
    /**
     * The actual argument elements for the {@link org.elixir_lang.psi.call.Call}.  Used for annotating the arguments.
     */
    PsiElement[] arguments();
}
