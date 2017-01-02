package org.elixir_lang.psi.call;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface CanonicallyNamed extends PsiElement {
    /**
     * @return {@code null} if it does not have a canonical name OR if it has more than one canonical name
     */
    @Nullable
    String canonicalName();

    /**
     * @return empty set if no canonical names
     */
    @NotNull
    Set<String> canonicalNameSet();
}
