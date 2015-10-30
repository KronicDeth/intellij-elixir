package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public abstract class NamedStubbedPsiElementBase<T extends StubElement<?>> extends StubbedPsiElementBase<T> implements PsiNameIdentifierOwner {
    public NamedStubbedPsiElementBase(@NotNull T stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public NamedStubbedPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public int getTextOffset() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getTextOffset() : super.getTextOffset();
    }
}
