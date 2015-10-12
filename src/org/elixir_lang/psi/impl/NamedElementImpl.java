package org.elixir_lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.elixir_lang.psi.NamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class NamedElementImpl extends ASTWrapperPsiElement implements NamedElement {
    public NamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
