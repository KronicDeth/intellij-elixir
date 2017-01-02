package org.elixir_lang.beam.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.ArrayFactory;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import org.elixir_lang.beam.psi.Module;
import org.elixir_lang.psi.Modular;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.MaybeExported;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.elixir_lang.beam.psi.stubs.ModuleStubElementTypes.CALL_DEFINITION;

// See com.intellij.psi.impl.compiled.ClsClassImpl
public class ModuleImpl<T extends StubElement> extends ModuleElementImpl implements Module, StubBasedPsiElement<T> {
    private final T stub;

    public ModuleImpl(T stub) {
        this.stub = stub;
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

        setMirrors(exports(), exports(element));
    }

    @Contract(pure = true)
    @NotNull
    public static MaybeExported[] exports(@NotNull TreeElement mirror) {
        PsiElement mirrorPsi = mirror.getPsi();
        MaybeExported[] exports;

        if (mirrorPsi instanceof Call) {
            Call mirrorCall = (Call) mirrorPsi;
            final List<MaybeExported> exportedList = new ArrayList<MaybeExported>();

            Modular.callDefinitionClauseCallWhile(mirrorCall, new Function<Call, Boolean>() {
                        @Override
                        public Boolean fun(Call call) {
                            if (call instanceof MaybeExported) {
                                MaybeExported maybeExportedCall = (MaybeExported) call;

                                if (maybeExportedCall.isExported()) {
                                    exportedList.add(maybeExportedCall);
                                }
                            }

                            return true;
                        }
                    }
            );

            exports = exportedList.toArray(new MaybeExported[exportedList.size()]);
        } else {
            exports = new MaybeExported[0];
        }

        return exports;
    }

    private MaybeExported[] exports() {
        return (MaybeExported[]) getStub().getChildrenByType(
                CALL_DEFINITION,
                new ArrayFactory() {
                    @NotNull
                    @Override
                    public MaybeExported[] create(int count) {
                        return new CallDefinitionImpl[count];
                    }
                }
        );
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

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    /**
     * Renames the element.
     *
     * @param name the new element name.
     * @return the element corresponding to this element after the rename (either <code>this</code>
     * or a different element if the rename caused the element to be replaced).
     * @throws IncorrectOperationException if the modification is not supported or not possible for some reason.
     */
    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Cannot modify module name in Beam files");
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return getMirror();
    }
}
