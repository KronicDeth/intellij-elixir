package org.elixir_lang.beam.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.elixir_lang.beam.psi.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// See com.intellij.psi.impl.compiled.ClsClassImpl
public class ModuleImpl<T extends StubElement> extends ModuleElementImpl implements Module, StubBasedPsiElement<T> {
    private final T stub;

    public ModuleImpl(T stub) {
        this.stub = stub;
    }

    public ModuleImpl(ASTNode node) {
        this((T) null);
    }

    /**
     * Returns the array of children for the PSI element.
     * Important: In some implementations children are only composite elements, i.e. not a leaf elements
     *
     * @return the array of child elements.
     */
    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return new PsiElement[0];
    }

    /**
     * Returns the parent of the PSI element.
     *
     * @return the parent of the element, or null if the element has no parent.
     */
    @Override
    public PsiElement getParent() {
        return stub.getParentStub().getPsi();
    }

    @Override
    public IStubElementType getElementType() {
        return stub.getStubType();
    }

    @Override
    public T getStub() {
        return stub;
    }

    @Override
    public void appendMirrorText(@NotNull StringBuilder buffer, int indentLevel) {
        assert buffer != null;
    }

    @Override
    public void setMirror(@NotNull TreeElement element) throws InvalidMirrorException {
        setMirrorCheckingType(element, null);
    }

    /**
     * @return {@code null} if it does not have a canonical name OR if it has more than one canonical name
     */
    @Nullable
    @Override
    public String canonicalName() {
        assert stub != null;

        return null;
    }

    /**
     * @return empty set if no canonical names
     */
    @NotNull
    @Override
    public Set<String> canonicalNameSet() {
        assert stub != null;

        return null;
    }
}
