package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An element that contains a module attribute name; either a declaration or a usage.
 */
public interface ModuleAttributeNameable extends PsiElement {
    @Contract(pure = true)
    @NotNull
    String moduleAttributeName();
}
